package xxs.common.module.codegenerate;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import xxs.common.module.codegenerate.cache.TableInfoTemCache;
import xxs.common.module.codegenerate.model.ColumnInfo;
import xxs.common.module.codegenerate.model.SearchColumnInfo;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.sql.DruidSqlDisposeUtils;

import java.sql.*;
import java.util.*;

/**
 * https://www.runoob.com/manual/jdk11api/java.sql/java/sql/DatabaseMetaData.html 看元数据（在ResultSet对象下的metadata是这个查询返回的列有那些 名字是什么等。，rows时具体的数据）
 * 基于数据库的表处理服务
 *
 * @author xxs
 */
@Slf4j
public class DBTableServiceImpl implements TableService {
    @Override
    public Map<String, TableInfo> loadTables(String tableNames) throws SQLException {
        return this.loadTables(tableNames, null);
    }

    /**
     * 加载表信息
     *
     * @param tableNames
     * @param replaceTablePre 移除生成的驼峰表名前缀
     * @return
     * @throws SQLException
     */
    @Override
    public Map<String, TableInfo> loadTables(String tableNames, String replaceTablePre) throws SQLException {
        Map<String, TableInfo> tableInfoHashMap = new HashMap<>(8);
        Connection connection = DruidConnectionPoolUtils.getConnection();
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
                    tableInfo.setComment(StringUtils.isNotEmpty(comment) ? comment : name);
                    tableInfo.setTableType(tableType);
                    String replaceTablePreName = name;
                    if (StringUtils.isNotEmpty(replaceTablePre)) {
                        replaceTablePreName = StrUtil.removePrefix(name.toUpperCase(), replaceTablePre.toUpperCase());
                    }

                    String camelCaseTableName = StrUtil.toCamelCase(replaceTablePreName);
                    tableInfo.setTableName(camelCaseTableName);
                    //首字母大写
                    tableInfo.setCapitalizeTableName(StringUtils.capitalize(camelCaseTableName));
                    ResultSet columnResultSet = metaData.getColumns(null, "xxs", tableName,
                            "%");
                    //找出列信息
                    while (columnResultSet.next()) {
                        this.buildColumn(columnInfoList, columnResultSet);
                    }
                    //找出主键列
                    this.buildKeyColumn(metaData, tableName, tableInfo, columnInfoList);
                    tableInfo.setColumnInfos(columnInfoList);
                }
                tableInfoHashMap.put(tableName, tableInfo);
            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            DruidConnectionPoolUtils.closeConnection(connection);
        }
        return tableInfoHashMap;
    }

    private void buildKeyColumn(DatabaseMetaData metaData, String tableName, TableInfo tableInfo, List<ColumnInfo> columnInfoList) throws SQLException {
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

    private void buildColumn(List<ColumnInfo> columnInfoList, ResultSet columnResultSet) throws SQLException {
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
        columnInfo.setComment(StringUtils.isNotEmpty(remarks) ? remarks : columnName);
        /*   String columnDef = columnResultSet.getString("COLUMN_DEF");  //默认值*/
        columnInfoList.add(columnInfo);
    }

    /**
     * 根据查询sql得到查询的sql的列的信息
     * TODO 如果这个sql有三个相同表名，那么字段名称会重复比如内连接
     *
     * @param sql
     * @return
     */
    @Override
    public List<SearchColumnInfo> getSearchColumnInfoBySearchSql(String sql) {
        String realString = DruidSqlDisposeUtils.setSelectLimit(sql);
        List<SearchColumnInfo> searchColumnInfoList = new ArrayList<>();
        try (Connection con = DruidConnectionPoolUtils.getConnection()) {
            ResultSet resultSet = null;
            try (PreparedStatement statement = con.prepareStatement(realString)) {
                if (statement.execute()) {
                    resultSet = statement.getResultSet();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    Set<String> existColumnNames = new HashSet<>();
                    for (int columnIndex = 1; columnIndex < columnCount; columnIndex++) {
                        buildSearchColumnInfoList(searchColumnInfoList, metaData, existColumnNames, columnIndex);
                    }
                    this.wrapSearchColumnInfo(searchColumnInfoList);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String format = String.format("执行sql出错,sql: %s,msg: %s ", realString, e.getMessage());
                throw new RuntimeException(format);
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            return searchColumnInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchColumnInfoList;
    }

    private void buildSearchColumnInfoList(List<SearchColumnInfo> searchColumnInfoList, ResultSetMetaData metaData, Set<String> existColumnNames, int columnIndex) throws SQLException {
        SearchColumnInfo searchColumnInfo = new SearchColumnInfo();
        String columnName = metaData.getColumnName(columnIndex);
        int columnType = metaData.getColumnType(columnIndex);
        String columnClassName = metaData.getColumnClassName(columnIndex);
        String tableName = metaData.getTableName(columnIndex);
        boolean addExistColumn = existColumnNames.add(columnName);
        searchColumnInfo.setRealColumnName(columnName);
        if (addExistColumn == false) {
            // 如果列名已经存在，那么需要重写列名，并且，需要重写SQL为结果集和为指定的列名
            columnName = tableName + "_" + columnName;
            searchColumnInfo.setColumnNameRewrite(true);
        }
        searchColumnInfo.setTableName(tableName);
        String camelCaseColumnName = StrUtil.toCamelCase(columnName);
        //首字母大写
        String capitalizeColumnName = StringUtils.capitalize(camelCaseColumnName);
        searchColumnInfo.setColumnName(columnName);
        searchColumnInfo.setJdbcTypeCode(columnType);
        searchColumnInfo.setJdbcTypeName(columnClassName);
        searchColumnInfo.setCapitalizeColumnName(capitalizeColumnName);
        searchColumnInfo.setCamelCaseColumnName(camelCaseColumnName);
        searchColumnInfoList.add(searchColumnInfo);
    }

    private void wrapSearchColumnInfo(List<SearchColumnInfo> searchColumnInfoList) throws Exception {
        if (!CollectionUtils.isEmpty(searchColumnInfoList)) {
            TableInfoTemCache tableInfoTemCache = new TableInfoTemCache(this);
            for (SearchColumnInfo searchColumnInfo : searchColumnInfoList) {
                String tableName = searchColumnInfo.getTableName();
                TableInfo tableInfo = tableInfoTemCache.getTableInfo(tableName);
                if (tableInfo == null) {
                    log.warn("wrapSearchColumnInfo  table no exist {}", tableName);
                    continue;
                }
                List<ColumnInfo> columnInfos = tableInfo.getColumnInfos();
                if (CollectionUtils.isEmpty(columnInfos)) {
                    log.warn("wrapSearchColumnInfo  table no exist {}", tableName);
                    continue;
                }
                inner:
                for (ColumnInfo columnInfo : columnInfos) {
                    if (columnInfo.getColumnName().equals(searchColumnInfo.getColumnName())) {
                        searchColumnInfo.setComment(columnInfo.getComment());
                        break inner;
                    }
                }
                log.warn("wrapSearchColumnInfo  columnInfo no exist {}", searchColumnInfo.getColumnName());
            }
        }
    }

    public static void main(String[] args) {
        TableService dbTableService = new DBTableServiceImpl();
        List<SearchColumnInfo> searchColumnInfoBySearchSql = dbTableService.getSearchColumnInfoBySearchSql("select *,1 from perm_user_group a inner join perm_user_group_admin_relation b on a.user_group_id = b.user_group_id");
        System.out.println(searchColumnInfoBySearchSql);
    }
}
