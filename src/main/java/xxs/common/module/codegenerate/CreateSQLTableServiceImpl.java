package xxs.common.module.codegenerate;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.statement.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import xxs.common.module.InMemoryDataSource;
import xxs.common.module.codegenerate.cache.TableInfoTemCache;
import xxs.common.module.codegenerate.model.ColumnInfo;
import xxs.common.module.codegenerate.model.SearchColumnInfo;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;
import xxs.common.module.sql.DruidSqlDisposeUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于创建表语句表处理
 *
 * @author xxs
 */
@Slf4j
@Deprecated
public class CreateSQLTableServiceImpl implements TableService {
    private DbType dbType;
    private Map<String, TableInfo> tableInfoMap = new ConcurrentHashMap<>(16);
    private Map<String, String> createTableSqlMap = new ConcurrentHashMap<>(16);

    public CreateSQLTableServiceImpl(DbType dbType, List<String> initCreateTableSqls) {
        this.dbType = dbType;
        Assert.isTrue(dbType != null, "数据库类型不能为空");
        if (CollectionUtil.isNotEmpty(initCreateTableSqls)) {
            for (String createTableSql : initCreateTableSqls) {
                addTableInfo(createTableSql);
            }
        }
    }

    /**
     * 添加表信息
     *
     * @param createTableSql 建表语句
     */
    public void addTableInfo(String createTableSql) {
        TableInfo tableInfo = new TableInfo();
        List<ColumnInfo> tableColumnInfos = new ArrayList<>();
        Set<String> primaryKeyColumnNames = new HashSet<>();
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(createTableSql, dbType);
        if (sqlStatements != null && sqlStatements.size() == 1) {
            SQLStatement sqlStatement = sqlStatements.get(0);
            SQLCreateTableStatement createTableStatement = null;
            if (sqlStatement instanceof SQLCreateTableStatement) {
                createTableStatement = (SQLCreateTableStatement) sqlStatement;
                //初始化表基本信息
                this.initTableBaseInfo(tableInfo, createTableStatement);
                this.addTableInHsql(tableInfo.getName(), createTableSql);
                List<SQLTableElement> tableElementList = createTableStatement.getTableElementList();
                initTableColumnInfo(tableColumnInfos, primaryKeyColumnNames, tableElementList);
                //同步表级和列级的主键关系处理主键问题
                tableColumnInfos:
                for (ColumnInfo tableColumnInfo : tableColumnInfos) {
                    if (primaryKeyColumnNames.contains(tableColumnInfo.getColumnName())) {
                        tableColumnInfo.setKeyFlag(true);
                        tableInfo.setKeyColumnInfo(tableColumnInfo);
                        break tableColumnInfos;
                    }
                }
            }
        }
        tableInfo.setColumnInfos(tableColumnInfos);
        tableInfoMap.put(tableInfo.getName().toUpperCase(Locale.ROOT), tableInfo);
        createTableSqlMap.put(tableInfo.getName().toUpperCase(Locale.ROOT), createTableSql);
        //将表加入到内存中。会先删除之前相同名称的表 TODO 看看后续有没有可能 分shame
    }

    private void addTableInHsql(String tableName, String createTableSql) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = InMemoryDataSource.getConnection();
            statement = connection.createStatement();
            statement.execute(String.format("DROP TABLE IF EXISTS %s;", tableName));
            statement.execute(createTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化表列信息
     *
     * @param tableColumnInfos      列信息集合
     * @param primaryKeyColumnNames 主键列名
     * @param tableElementList      表元素集合
     */
    private void initTableColumnInfo(List<ColumnInfo> tableColumnInfos, Set<String> primaryKeyColumnNames, List<SQLTableElement> tableElementList) {
        for (SQLTableElement sqlTableElement : tableElementList) {
            //处理普通列
            if (sqlTableElement instanceof SQLColumnDefinition) {
                SQLColumnDefinition sqlColumnDefinition = (SQLColumnDefinition) sqlTableElement;
                ColumnInfo tableColumnInfo = new ColumnInfo();
                String columnName = sqlColumnDefinition.getColumnName();
                tableColumnInfo.setColumnName(sqlColumnDefinition.getColumnName());
                String camelCaseColumnName = StrUtil.toCamelCase(columnName);
                tableColumnInfo.setPropertyName(camelCaseColumnName);
                //首字母大写
                tableColumnInfo.setCapitalizePropertyName(StringUtils.capitalize(camelCaseColumnName));
                tableColumnInfo.setAutoincrement(sqlColumnDefinition.isAutoIncrement());
                SQLExpr columnComment = sqlColumnDefinition.getComment();
                if (columnComment != null) {
                    tableColumnInfo.setComment(columnComment.toString());
                }
                tableColumnInfo.setColumnSize(0);
                List<SQLColumnConstraint> constraints = sqlColumnDefinition.getConstraints();
                //处理约束 比如唯一约束什么的
                if (!CollectionUtils.isEmpty(constraints)) {
                    for (SQLColumnConstraint constraint : constraints) {
                        if (constraint instanceof SQLColumnPrimaryKey) {
                            tableColumnInfo.setKeyFlag(true);
                            primaryKeyColumnNames.add(sqlColumnDefinition.getColumnName());
                        }
                        if (constraint instanceof SQLColumnUniqueKey) {
                        }
                        if (constraint instanceof SQLNotNullConstraint) {
                            tableColumnInfo.setNullAble(false);
                        }
                    }
                }
                //处理类型信息
                SQLDataType sqlDataType = sqlColumnDefinition.getDataType();
                if (sqlDataType instanceof SQLDataTypeImpl) {
                    SQLDataTypeImpl dataType = (SQLDataTypeImpl) sqlDataType;
                    List<Object> argsValue = new ArrayList<>();
                    List<SQLExpr> arguments = sqlDataType.getArguments();
                    SQLDataTypeArguments:
                    for (SQLExpr argument : arguments) {
                        SQLValuableExpr sqlValuableExpr = (SQLValuableExpr) argument;
                        argsValue.add(sqlValuableExpr.getValue());
                        if (sqlValuableExpr instanceof SQLNumericLiteralExpr) {
                            SQLNumericLiteralExpr sqlNumericLiteralExpr = (SQLNumericLiteralExpr) sqlValuableExpr;
                            tableColumnInfo.setColumnSize(sqlNumericLiteralExpr.getNumber().intValue());
                            break SQLDataTypeArguments;
                        }
                    }
                    int jdbcCode = dataType.jdbcType();
                    tableColumnInfo.setJdbcTypeCode(jdbcCode);
                    tableColumnInfo.setJdbcTypeName(JdbcType.forCode(jdbcCode).name());
                    tableColumnInfo.setJavaType(TypeMapperRegistry.getJavaType(jdbcCode));

                }
                tableColumnInfos.add(tableColumnInfo);
            }
            //处理主键列，比如 "  PRIMARY KEY (`bid`) USING BTREE\n"
            if (sqlTableElement instanceof SQLPrimaryKey) {
                SQLPrimaryKey sqlPrimaryKey = (SQLPrimaryKey) sqlTableElement;
                List<SQLSelectOrderByItem> primaryKeys = sqlPrimaryKey.getColumns();
                if (!CollectionUtils.isEmpty(primaryKeys)) {
                    for (SQLSelectOrderByItem primaryKey : primaryKeys) {
                        SQLExpr primaryKeyExpr = primaryKey.getExpr();
                        if (primaryKeyExpr instanceof SQLIdentifierExpr) {
                            SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) primaryKeyExpr;
                            String primaryKeyName = sqlIdentifierExpr.getName();
                            primaryKeyColumnNames.add(primaryKeyName);
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化表基本信息
     *
     * @param tableInfo
     * @param createTableStatement
     */
    private void initTableBaseInfo(TableInfo tableInfo, SQLCreateTableStatement createTableStatement) {
        String simpleName = createTableStatement.getName().getSimpleName();
        SQLExpr comment = createTableStatement.getComment();
        if (comment != null) {
            tableInfo.setComment(comment.toString());
        }
        tableInfo.setName(simpleName);
        String camelCaseTableName = StrUtil.toCamelCase(simpleName);
        tableInfo.setTableName(camelCaseTableName);
        //首字母大写
        tableInfo.setCapitalizeTableName(StringUtils.capitalize(camelCaseTableName));
    }

    /**
     * 根据表名加载表信息
     *
     * @param tableNames
     * @return
     */
    @Override
    public Map<String, TableInfo> loadTables(String tableNames) throws Exception {
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
    public Map<String, TableInfo> loadTables(String tableNames, String replaceTablePre) throws Exception {
        Map<String, TableInfo> tableInfoHashMap = new HashMap<>(8);
        String[] tableNameArr = tableNames.split(",");
        for (String tableName : tableNameArr) {
            TableInfo tableInfo = tableInfoMap.get(tableName);
            Assert.isTrue(tableInfo != null, String.format("%s表信息不存在！！！", tableName));
            this.replaceTablePre(replaceTablePre, tableInfo);
            tableInfoHashMap.put(tableName, tableInfo);
        }
        return tableInfoHashMap;
    }

    /**
     * 替换表前缀
     *
     * @param replaceTablePre
     * @param tableInfo
     */
    private void replaceTablePre(String replaceTablePre, TableInfo tableInfo) {
        String name = tableInfo.getName();
        String replaceTablePreName = name;
        if (StringUtils.isNotEmpty(replaceTablePre)) {
            replaceTablePreName = StrUtil.removePrefix(name.toUpperCase(), replaceTablePre.toUpperCase());
        }

        String camelCaseTableName = StrUtil.toCamelCase(replaceTablePreName);
        tableInfo.setTableName(camelCaseTableName);
        //首字母大写
        tableInfo.setCapitalizeTableName(StringUtils.capitalize(camelCaseTableName));
    }

    /**
     * 根据查询sql得到查询的sql的列的信息
     *
     * @param sql
     * @return
     */
    @Override
    public List<SearchColumnInfo> getSearchColumnInfoBySearchSql(String sql) {
        String realString = DruidSqlDisposeUtils.setSelectLimit1(sql);
        List<SearchColumnInfo> searchColumnInfoList = new ArrayList<>();
        try (Connection con = InMemoryDataSource.getConnection()) {
            ResultSet resultSet = null;
            try (PreparedStatement statement = con.prepareStatement(realString)) {
                if (statement.execute()) {
                    resultSet = statement.getResultSet();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    Set<String> existColumnNames = new HashSet<>();
                    for (int columnIndex = 1; columnIndex < columnCount; columnIndex++) {
                        this.buildSearchColumnInfoList(searchColumnInfoList, metaData, existColumnNames, columnIndex);
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
    public static void main(String[] args) throws Exception {
        CreateSQLTableServiceImpl createSQLTableService = new CreateSQLTableServiceImpl(DbType.mysql, Arrays.asList("CREATE TABLE STUDENT (\n" +
                "  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '学生ID',\n" +
                "  name VARCHAR(50) NOT NULL COMMENT '学生姓名',\n" +
                "  age TINYINT UNSIGNED NOT NULL COMMENT '学生年龄',\n" +
                "  gender ENUM('MALE', 'FEMALE') NOT NULL DEFAULT 'MALE' COMMENT '学生性别',\n" +
                "  grade SMALLINT UNSIGNED NOT NULL COMMENT '学生年级',\n" +
                "  class_name VARCHAR(20) NOT NULL COMMENT '学生班级',\n" +
                "  address VARCHAR(100) NOT NULL COMMENT '学生地址',\n" +
                "  phone VARCHAR(20) NOT NULL COMMENT '学生电话',\n" +
                "  email VARCHAR(50) COMMENT '学生邮箱',\n" +
                "  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "  PRIMARY KEY (id)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表1';\n"));
        Map<String, TableInfo> tableInfoMap = createSQLTableService.loadTables("STUDENT");
        System.out.println(tableInfoMap);
    }
}
