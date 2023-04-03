package xxs.common.module.codegenerate;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import xxs.common.module.codegenerate.config.DataSourceConfig;
import xxs.common.module.codegenerate.model.ColumnInfo;
import xxs.common.module.codegenerate.model.TableInfo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://www.runoob.com/manual/jdk11api/java.sql/java/sql/DatabaseMetaData.html 看元数据（在ResultSet对象下的metadata是这个查询返回的列有那些 名字是什么等。，rows时具体的数据）
 *
 * @author xxs
 */
public class LoadTableService {
    private DataSourceConfig dataSourceConfig;

    public LoadTableService(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public  Map<String, TableInfo> loadTables(String tableNames) throws SQLException {
        Map<String, TableInfo> tableInfoHashMap = new HashMap<>();
        JdbcUtils jdbcUtils = new JdbcUtils(dataSourceConfig.getDriverClassName(), dataSourceConfig.getJdbcUrl(), dataSourceConfig.getJdbcUsername(), dataSourceConfig.getJdbcPassword());
        Connection connection = jdbcUtils.getConnection();
        connection.setAutoCommit(false);
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String[] tableNameArr = tableNames.split(",");
            for (String tableName : tableNameArr) {
                ResultSet tableResultSet = metaData.getTables(null, null, tableName,
                        null);
                TableInfo tableInfo = new TableInfo();
                List<ColumnInfo> columnInfoList = new ArrayList<>();
                //找出表信息
                while (tableResultSet.next()) {
                    //表名
                    String name = tableResultSet.getString("TABLE_NAME");
                    //表类型
                    String tableType = tableResultSet.getString("TABLE_TYPE");
                    //表备注
                    String comment = tableResultSet.getString("REMARKS");
                    tableInfo.setName(name);
                    tableInfo.setComment(comment);
                    tableInfo.setTableType(tableType);
                    String camelCaseTableName = StrUtil.toCamelCase(name);
                    tableInfo.setTableName(camelCaseTableName);
                    //首字母大写
                    tableInfo.setCapitalizeTableName(StringUtils.capitalize(camelCaseTableName));
                    ResultSet columnResultSet = metaData.getColumns(null, "xxs", tableName,
                            "%");
                    //找出列信息
                    while (columnResultSet.next()) {
                        buildColumn(columnInfoList, columnResultSet);
                    }
                    //找出主键列
                    buildKeyColumn(metaData, tableName, tableInfo, columnInfoList);
                    tableInfo.setColumnInfos(columnInfoList);
                }
                tableInfoHashMap.put(tableName, tableInfo);
            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            jdbcUtils.closeConnection(connection);
        }
        return tableInfoHashMap;
    }

    private  void buildKeyColumn(DatabaseMetaData metaData, String tableName, TableInfo tableInfo, List<ColumnInfo> columnInfoList) throws SQLException {
        ResultSet keyResultSet = metaData.getPrimaryKeys(null, null, tableName);
        while (keyResultSet.next()) {
            //主键列名
            String columnName = keyResultSet.getString("COLUMN_NAME");
            columnInfoList:
            for (ColumnInfo columnInfo : columnInfoList) {
                if (columnName.equalsIgnoreCase(columnInfo.getColumnName())) {
                    columnInfo.setKeyFlag(true);
                    //设置主键列
                    tableInfo.setKeyColumnInfo(columnInfo);
                    break columnInfoList;
                }
            }

        }
    }

    private  void buildColumn(List<ColumnInfo> columnInfoList, ResultSet columnResultSet) throws SQLException {
        ColumnInfo columnInfo = new ColumnInfo();
        //列名
        String columnName = columnResultSet.getString("COLUMN_NAME");
        columnInfo.setColumnName(columnName);
        //对应的java.sql.Types的SQL类型(列类型ID)
        int dataTypeCode = columnResultSet.getInt("DATA_TYPE");
        //java.sql.Types类型名称(列类型名称)
        String dataTypeName = columnResultSet.getString("TYPE_NAME");
        // 是否是自增长
        String isAutoincrement = columnResultSet.getString("IS_AUTOINCREMENT");
        // 列的长度
        int columnSize = columnResultSet.getInt("COLUMN_SIZE");
        columnInfo.setColumnSize(columnSize);
        columnInfo.setAutoincrement("YES".equalsIgnoreCase(isAutoincrement));
        columnInfo.setJdbcTypeCode(dataTypeCode);
        columnInfo.setJdbcTypeName(dataTypeName);
        columnInfo.setJavaType(TypeMapperRegistry.getJavaType(dataTypeCode));
        String camelCaseColumnName = StrUtil.toCamelCase(columnName);
        columnInfo.setPropertyName(camelCaseColumnName);
        //首字母大写
        columnInfo.setCapitalizePropertyName(StringUtils.capitalize(camelCaseColumnName));
        /* int columnSize = columnResultSet.getInt("COLUMN_SIZE");  //列大小*/
        /**
         *  0 (columnNoNulls) - 该列不允许为空
         *  1 (columnNullable) - 该列允许为空
         *  2 (columnNullableUnknown) - 不确定该列是否为空
         */
        //是否允许为null
        int nullAble = columnResultSet.getInt("NULLABLE");
        columnInfo.setNullAble(nullAble == 1);
        //列描述
        String remarks = columnResultSet.getString("REMARKS");
        columnInfo.setComment(remarks);
        /*   String columnDef = columnResultSet.getString("COLUMN_DEF");  //默认值*/
        columnInfoList.add(columnInfo);
    }
}
