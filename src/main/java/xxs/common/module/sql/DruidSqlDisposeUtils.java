package xxs.common.module.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;

import java.util.List;

/**
 * @author issuser
 */
public class DruidSqlDisposeUtils {
    public static void main(String[] args) {
        String sql = "  select  phone from 1shujuzhiliang01 a inner join  1shujuzhiliang01 b on a.a=b.b  inner join  1shujuzhiliang01 c on c.a=b.b  where  (phone regexp '^1([3578][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$')= false and id in (select * from 1shujuzhiliang01 aa inner join 1shujuzhiliang01 bb on aa.a=bb.b)";
        String sql2 = "with test_with as(select * from 1shujuzhiliang1 a inner join shujuzhiliang b on a.a=b.b inner join shujuzhiliang c on c.a=c.c  ) select * from B inner join shujuzhiliang A on A.a=B.b  inner join shujuzhiliang C on C.a=B.b  inner join (select * from shujuzhiliang E inner join shujuzhiliang F on F.a=E.a) D on D.a=B.b where " +
                "id in (select * from shujuzhiliang z inner join shujuzhiliang q on z.id=q.id)";
        String sql3="select * from student2 where id < 4\n" +
                "\n" +
                "union all\n" +
                "\n" +
                "select * from student2 where id > 2 and id < 6";
        String result = DruidSqlDisposeUtils.processCheckSqlWhere(sql, "a=1 or b=1 ", "1shujuzhiliang01", JdbcConstants.HIVE.name());
        System.out.println(result);
    }

    /**
     * 添加where条件
     */
    public static String processCheckSqlWhere(String checkSql, String whereSqlBlock, String tableName, String dbType) {
        SelectDisposeWhereBlock selectDisposeWhereBlock = new SelectDisposeWhereBlock(checkSql, whereSqlBlock, tableName, dbType);
        String result = checkSql;
        try {
            result = selectDisposeWhereBlock.processSelectBody();
        } catch (Exception e) {
            e.printStackTrace();
            //尝试使用mysql的语法解析
            if (JdbcConstants.MYSQL.equals(dbType)) {
                throw new RuntimeException(e);
            }
            SelectDisposeWhereBlock mysqlSelectDisposeWhereBlock = new SelectDisposeWhereBlock(checkSql, whereSqlBlock, tableName, JdbcConstants.MYSQL.name());
            return mysqlSelectDisposeWhereBlock.processSelectBody();
        }
        return result;
    }

    /**
     * 简单验证SQL
     */
    public static boolean validSql(String checkSql, String dbType) {
        try {
            SQLUtils.parseStatements(checkSql, dbType);
        } catch (Exception e) {
            e.printStackTrace();
            //尝试使用mysql的语法解析
            if (JdbcConstants.MYSQL.equals(dbType)) {
                return false;
            }
            try {
                SQLUtils.parseStatements(checkSql, JdbcConstants.MYSQL);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }

        }
        return true;
    }

    static class SelectDisposeWhereBlock {
        private String tableName;
        private String whereSqlBlock;
        private String selectSql;
        private String dbType;

        public SelectDisposeWhereBlock(String selectSql, String whereSqlBlock, String tableName, String dbType) {
            this.selectSql = selectSql;
            this.whereSqlBlock = whereSqlBlock;
            this.tableName = tableName;
            this.dbType = StringUtils.isEmpty(dbType) ? JdbcConstants.MYSQL.name() : dbType;
        }

        /**
         * 解析处理SQL
         */
        public String processSelectBody() {
            //解析单条sql
            SQLStatement sqlStatement = SQLUtils.parseSingleStatement(selectSql, dbType);
            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
            SQLSelect select = sqlSelectStatement.getSelect();
            //处理主SQL
            doSqlSelect(select);
            SQLWithSubqueryClause withSubQuery = select.getWithSubQuery();
            //处理with
            if (withSubQuery != null) {
                List<SQLWithSubqueryClause.Entry> entries = withSubQuery.getEntries();
                if (entries != null) {
                    for (SQLWithSubqueryClause.Entry entry : entries) {
                        SQLSelect subQuery = entry.getSubQuery();
                        doSqlSelect(subQuery);
                    }
                }
            }
            return SQLUtils.toSQLString(select, dbType);
        }

        public void doSqlSelect(SQLSelect sqlSelect) {
            if (sqlSelect.getQuery() instanceof SQLUnionQuery) {
                SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelect.getQuery();
                //如果是union
                List<SQLSelectQuery> sqlSelectQueries = sqlUnionQuery.getRelations();
                for (SQLSelectQuery sqlSelectQuery : sqlSelectQueries) {
                    if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
                        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
                        doSQLSelectQueryBlock(queryBlock);
                    }
                }
            } else {
                SQLSelectQueryBlock queryBlock = sqlSelect.getQueryBlock();
                //如果不是union
                doSQLSelectQueryBlock(queryBlock);
            }

        }


        private SQLSelectQueryBlock findSQLSelectQueryBlock(SQLObject sqlObject) {
            SQLObject parent = sqlObject.getParent();
            if (parent == null) {
                return null;
            }
            if (parent instanceof SQLSelectQueryBlock) {
                return (SQLSelectQueryBlock) parent;
            }
            return this.findSQLSelectQueryBlock(parent);
        }

        public void doSQLExprTableSource(SQLExprTableSource sqlExprTableSource) {
            SQLIdentifierExpr expr = (SQLIdentifierExpr) sqlExprTableSource.getExpr();
            String originTableName = expr.getName();
            if (originTableName.equals(String.format("%s", tableName)) || originTableName.equals(String.format("`%s`", tableName))) {
                SQLSelectQueryBlock sqlSelectQueryBlock = findSQLSelectQueryBlock(sqlExprTableSource);
                if (sqlSelectQueryBlock != null) {
                    String alias = sqlExprTableSource.getAlias();
                    sqlSelectQueryBlock.addWhere(parseWhere(whereSqlBlock, alias));
                }
            }
        }
        private void whereSubQuery(SQLExpr sqlExpr) {
            if (sqlExpr instanceof SQLInSubQueryExpr) {
                SQLInSubQueryExpr inSubQueryExpr = (SQLInSubQueryExpr) sqlExpr;
                SQLSelect subQuery = inSubQueryExpr.getSubQuery();
                doSQLSelectQueryBlock(subQuery.getFirstQueryBlock());
            } else if (sqlExpr instanceof SQLQueryExpr) {
                SQLQueryExpr queryExpr = (SQLQueryExpr) sqlExpr;
                SQLSelect subQuery = queryExpr.getSubQuery();
                doSQLSelectQueryBlock(subQuery.getFirstQueryBlock());
            } else if (sqlExpr instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr opExpr = (SQLBinaryOpExpr) sqlExpr;

                SQLExpr leftExpr = opExpr.getLeft();
                if (leftExpr != null) {
                    whereSubQuery(leftExpr);
                }

                SQLExpr rightExpr = opExpr.getRight();
                if (rightExpr != null) {
                    whereSubQuery(rightExpr);
                }
            }
        }
        public void doSQLSelectQueryBlock(SQLSelectQueryBlock queryBlock) {
            if (queryBlock == null) {
                return;
            }
            SQLTableSource sqlTableSource = queryBlock.getFrom();
            getTableInfo(sqlTableSource);
            if (queryBlock != null) {
                SQLExpr where = queryBlock.getWhere();
                whereSubQuery(where);
            }
        }

        private void addWhereSqlBlock(SQLSelectQueryBlock queryBlock, String alias) {
            queryBlock.addWhere(parseWhere(whereSqlBlock, alias));
        }

        private void getTableInfo(SQLTableSource sqlTableSource) {
            if (sqlTableSource instanceof SQLExprTableSource) {
                SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) sqlTableSource;
                doSQLExprTableSource(sqlExprTableSource);
            }
            if (sqlTableSource instanceof SQLSubqueryTableSource) {
                SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) sqlTableSource;
                SQLSelect select = sqlSubqueryTableSource.getSelect();
                doSqlSelect(select);
            }
            if (sqlTableSource instanceof SQLUnionQueryTableSource) {
                SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource) sqlTableSource;
                SQLUnionQuery unionSql = sqlUnionQueryTableSource.getUnion();
                //如果是union
                List<SQLSelectQuery> sqlSelectQueries = unionSql.getRelations();
                for (SQLSelectQuery sqlSelectQuery : sqlSelectQueries) {
                    if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
                        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
                        doSQLSelectQueryBlock(queryBlock);
                    }
                }
            }
            if (sqlTableSource instanceof SQLJoinTableSource) {
                SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) sqlTableSource;
                getTableInfo(sqlJoinTableSource.getLeft());
                getTableInfo(sqlJoinTableSource.getRight());
            }
        }
    }

    public static SQLExpr parseWhere(String whereString, String alias) {
        SQLExprParser sqlExprParser = new SQLExprParser("where " + whereString);
        sqlExprParser.accept(Token.WHERE);
        SQLExpr expr = sqlExprParser.expr();
        if (alias == null) {
            return expr;
        }
        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr conditionColumnExpr = (SQLBinaryOpExpr) expr;
            SQLExpr left = conditionColumnExpr.getLeft();
            SQLExpr right = conditionColumnExpr.getRight();
            if (left != null) {
                parseSQLBinaryOpExpr(left, alias);
            }
            if (right != null) {
                parseSQLBinaryOpExpr(right, alias);
            }
        } else {
            List<SQLObject> childrenList = expr.getChildren();
            for (SQLObject sqlObject : childrenList) {
                // 包含了 left 和 right
                SQLBinaryOpExpr conditionBinary = (SQLBinaryOpExpr) sqlObject;
                parseSQLBinaryOpExpr(conditionBinary, alias);
            }
        }
        return expr;
    }


    public static void parseSQLBinaryOpExpr(SQLExpr expr, String alias) {
        // 如果没有有别名
        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr conditionColumnExpr2 = (SQLIdentifierExpr) expr;
            conditionColumnExpr2.setName(alias + "." + conditionColumnExpr2.getName());
        }
        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr conditionColumnExpr = (SQLBinaryOpExpr) expr;
            SQLExpr left = conditionColumnExpr.getLeft();
            SQLExpr right = conditionColumnExpr.getRight();
            if (left != null) {
                parseSQLBinaryOpExpr(left, alias);
            }
            if (right != null) {
                parseSQLBinaryOpExpr(right, alias);
            }
        }
    }
}

