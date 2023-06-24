package xxs.common.module.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.statement.*;
import org.springframework.util.CollectionUtils;
import xxs.common.module.datagenerate.db.dto.ColumnConstraint;
import xxs.common.module.datagenerate.db.dto.JdbcTypeInfo;
import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.datagenerate.db.dto.TableInfo;
import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//问题1 是否可以区分多主键列？
/*解析创建语句sql得到列的信息*/
public final class CreateTableSQLParseUtils {
    //根据创建语句解析出列的信息。
    public static TableInfo getTableInfo(DbType dbType, String createSql) {
        TableInfo tableInfo = new TableInfo();
        List<TableColumnInfo> tableColumnInfos = new ArrayList<>();
        Set<String> primaryKeyColumns = new HashSet<>();
        tableInfo.setPrimaryKeyColumns(primaryKeyColumns);
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(createSql, dbType);
        if (sqlStatements != null && sqlStatements.size() == 1) {
            SQLStatement sqlStatement = sqlStatements.get(0);
            SQLCreateTableStatement createTableStatement = null;
            if (sqlStatement instanceof SQLCreateTableStatement) {
                createTableStatement = (SQLCreateTableStatement) sqlStatement;

                String simpleName = createTableStatement.getName().getSimpleName();
                SQLExpr comment = createTableStatement.getComment();
                if (comment != null) {
                    tableInfo.setComment(comment.toString());
                }
                tableInfo.setTableName(simpleName);
                List<SQLTableElement> tableElementList = createTableStatement.getTableElementList();
                for (SQLTableElement sqlTableElement : tableElementList) {
                    //处理普通列
                    if (sqlTableElement instanceof SQLColumnDefinition) {
                        /*                        SQLDataType*/
                        SQLColumnDefinition sqlColumnDefinition = (SQLColumnDefinition) sqlTableElement;
                        TableColumnInfo tableColumnInfo = new TableColumnInfo();
                        tableColumnInfo.setColumnInfo(sqlColumnDefinition.toString());
                        tableColumnInfo.setColumnName(sqlColumnDefinition.getColumnName());
                        tableColumnInfo.setAutoincrement(sqlColumnDefinition.isAutoIncrement());
                        SQLExpr columnComment = sqlColumnDefinition.getComment();
                        if(columnComment!=null){
                            tableColumnInfo.setComment(columnComment.toString());
                        }
                        List<SQLColumnConstraint> constraints = sqlColumnDefinition.getConstraints();
                        List<ColumnConstraint> columnConstraints = new ArrayList<>();
                        //处理约束 比如唯一约束什么的
                        if (!CollectionUtils.isEmpty(constraints)) {
                            for (SQLColumnConstraint constraint : constraints) {
                                if (constraint instanceof SQLColumnPrimaryKey) {
                                    tableColumnInfo.setPrimaryKey(true);
                                    columnConstraints.add(ColumnConstraint.PRIMARY_KEY);
                                    primaryKeyColumns.add(sqlColumnDefinition.getColumnName());
                                }
                                if (constraint instanceof SQLColumnUniqueKey) {
                                    columnConstraints.add(ColumnConstraint.UNIQUE_KEY);
                                }
                                if (constraint instanceof SQLNotNullConstraint) {
                                    columnConstraints.add(ColumnConstraint.NOT_NULL);
                                }
                            }
                            tableColumnInfo.setColumnConstraints(columnConstraints);
                        }
                        //处理类型信息
                        SQLDataType sqlDataType = sqlColumnDefinition.getDataType();
                        if (sqlDataType instanceof SQLDataTypeImpl) {
                            SQLDataTypeImpl dataType = (SQLDataTypeImpl) sqlDataType;
                            JdbcTypeInfo jdbcTypeInfo = new JdbcTypeInfo();
                            jdbcTypeInfo.setJdbcType(JdbcType.forCode(dataType.jdbcType()));
                            List<Object> argsValue = new ArrayList<>();
                            List<SQLExpr> arguments = sqlDataType.getArguments();
                            for (SQLExpr argument : arguments) {
                                SQLValuableExpr sqlValuableExpr = (SQLValuableExpr) argument;
                                argsValue.add(sqlValuableExpr.getValue());
                            }
                            jdbcTypeInfo.setArguments(argsValue);
                            tableColumnInfo.setJdbcTypeInfo(jdbcTypeInfo);
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
                                    primaryKeyColumns.add(primaryKeyName);
                                }
                            }
                        }
                    }
                }
                //同步表级和列级的主键关系处理主键问题
                for (TableColumnInfo tableColumnInfo : tableColumnInfos) {
                    if (primaryKeyColumns.contains(tableColumnInfo.getColumnName())) {
                        tableColumnInfo.setPrimaryKey(true);
                    }
                }
            }
        }
        tableInfo.setTableColumnInfos(tableColumnInfos);
        return tableInfo;
    }

    public static void main(String[] args) {
        TableInfo tableInfo = CreateTableSQLParseUtils.getTableInfo(DbType.mysql,
                "CREATE TABLE  'student'(\n" +
                        " \t`id` int(4) zerofill unsigned NOT NULL AUTO_INCREMENT COMMENT '学号',\n" +
                        " \t`name` decimal(5,2) NOT NULL DEFAULT '匿名' COMMENT '姓名',\n" +
                        " \t`pwd` VARCHAR(20) NOT NULL DEFAULT '123456' COMMENT '密码',\n" +
                        " \t`sex` VARCHAR(2) NOT NULL DEFAULT '女' COMMENT '性别',\n" +
                        " \t`birthday` DATETIME DEFAULT NULL COMMENT '出生日期',\n" +
                        " \t`address` VARCHAR (100) DEFAULT NULL COMMENT '家庭地址',\n" +
                        " \t`email` VARCHAR(50) DEFAULT NULL COMMENT '电子邮箱',\n" +
                        " \tPRIMARY KEY(`id`)  " +
                        ")ENGINE=INNODB DEFAULT CHARSET=utf8 ");

    }
}
