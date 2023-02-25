package xxs.common.module.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author issuser
 */
public class DruidSqlDisposeUtilsV1 {
    public static void main(String[] args) {
        String sql = "  select  phone from 1shujuzhiliang01 a inner join  1shujuzhiliang01 b on a.a=b.b  inner join  1shujuzhiliang01 c on c.a=b.b  where  (phone regexp '^1([3578][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$')= false and id in (select * from 1shujuzhiliang01 aa inner join 1shujuzhiliang01 bb on aa.a=bb.b)";
        String sql2 = "with test_with as(select * from 1shujuzhiliang01 a inner join 1shujuzhiliang01 b on a.a=b.b inner join 1shujuzhiliang01 c on c.a=c.c  ) select * from B inner join 1shujuzhiliang01 A on A.a=B.b  inner join 1shujuzhiliang01 C on C.a=B.b  inner join (select * from 1shujuzhiliang01 E inner join 1shujuzhiliang01 F on F.a=E.a) D on D.a=B.b";

        String result = DruidSqlDisposeUtilsV1.processCheckSqlWhere("select ename,job from (select *from (select * from (select * from (select * from shujuzhiliang01) c) b) a ) emp where job = 'MANAGER'\n" +
                "union\n" +
                "select ename,job from  (select *from (select * from (select * from (select * from shujuzhiliang01) c) b) a ) emp where job = 'SALESMAN';", "a=1 ", "shujuzhiliang01", null);
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
            return SQLUtils.toSQLString(select, JdbcConstants.MYSQL);
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

        public void doSqlJoinTableSource(SQLTableSource from) {
            if (from != null) {
                if (from instanceof SQLExprTableSource) {
                    SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) from;
                    String originTableName = sqlExprTableSource.getExpr().toString();
                    if (originTableName.equals(String.format("%s", tableName)) || originTableName.equals(String.format("`%s`", tableName))) {
                        if (sqlExprTableSource.getParent() instanceof SQLSelectQueryBlock) {
                            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlExprTableSource.getParent();
                            SQLTableSource from1 = sqlSelectQueryBlock.getFrom();
                            String alias = from1.getAlias();
                            sqlSelectQueryBlock.addWhere(parseWhere(whereSqlBlock, alias));
                        }
                    }
                }
                if (from instanceof SQLJoinTableSource) {
                    SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) from;
                    SQLTableSource left = sqlJoinTableSource.getLeft();
                    if (left != null) {
                        SQLExprTableSource sqlExprTableSource = null;
                        if (left instanceof SQLExprTableSource) {
                            sqlExprTableSource = (SQLExprTableSource) left;
                            String originTableName = sqlExprTableSource.getExpr().toString();
                            if (originTableName.equals(String.format("%s", tableName)) || originTableName.equals(String.format("`%s`", tableName))) {
                                SQLSelectQueryBlock sqlSelectQueryBlock = findSQLSelectQueryBlock(sqlJoinTableSource);
                                if (sqlSelectQueryBlock != null) {
                                    String alias = sqlExprTableSource.getAlias();
                                    sqlSelectQueryBlock.addWhere(parseWhere(whereSqlBlock, alias));
                                }
                            }
                        }
                        if (left instanceof SQLJoinTableSource) {
                            this.doSqlJoinTableSource(left);
                        }
                        if (left instanceof SQLSubqueryTableSource) {
                            SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) left;
                            SQLSelect select = sqlSubqueryTableSource.getSelect();
                            doSqlSelect(select);
                        }
                        if (left instanceof SQLUnionQueryTableSource) {
                            SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource) left;
                            SQLUnionQuery unionSql = sqlUnionQueryTableSource.getUnion();

                            List<SQLSelectQuery> sqlSelectQueries = unionSql.getRelations();
                            if (!CollectionUtils.isEmpty(sqlSelectQueries)) {
                                for (SQLSelectQuery sqlSelectQuery : sqlSelectQueries) {
                                    if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
                                        SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
                                        doSQLSelectQueryBlock(sqlSelectQueryBlock);
                                    }

                                }
                            }
                        }
                    }
                    SQLTableSource right = sqlJoinTableSource.getRight();
                    if (right != null) {
                        SQLExprTableSource sqlExprTableSource = null;
                        if (right instanceof SQLExprTableSource) {
                            sqlExprTableSource = (SQLExprTableSource) right;
                            String originTableName = sqlExprTableSource.getExpr().toString();
                            if (originTableName.equals(String.format("%s", tableName)) || originTableName.equals(String.format("`%s`", tableName))) {
                                SQLSelectQueryBlock sqlSelectQueryBlock = findSQLSelectQueryBlock(sqlJoinTableSource);
                                if (sqlSelectQueryBlock != null) {
                                    String alias = sqlExprTableSource.getAlias();
                                    sqlSelectQueryBlock.addWhere(parseWhere(whereSqlBlock, alias));
                                }
                            }
                        }
                        if (right instanceof SQLJoinTableSource) {
                            this.doSqlJoinTableSource(right);
                        }
                        if (right instanceof SQLSubqueryTableSource) {
                            SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) right;
                            SQLSelect select = sqlSubqueryTableSource.getSelect();
                            doSqlSelect(select);
                        }
                        if (right instanceof SQLUnionQueryTableSource) {
                            SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource) right;
                            SQLUnionQuery unionSql = sqlUnionQueryTableSource.getUnion();
                            SQLSelectQueryBlock firstQueryBlock = unionSql.getFirstQueryBlock();
                            doSQLSelectQueryBlock(firstQueryBlock);
                        }
                    }
                }
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
            String originTableName = sqlExprTableSource.getExpr().toString();
            if (originTableName.equals(String.format("%s", tableName)) || originTableName.equals(String.format("`%s`", tableName))) {
                if (sqlExprTableSource.getParent() instanceof SQLSelectQueryBlock) {
                    SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlExprTableSource.getParent();
                    SQLTableSource from = sqlSelectQueryBlock.getFrom();
                    String alias = from.getAlias();
                    sqlSelectQueryBlock.addWhere(parseWhere(whereSqlBlock, alias));
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
                if (where != null) {
                    where.accept(new SQLASTVisitorAdapter() {
                        @Override
                        public boolean visit(SQLExprTableSource sqlExprTableSource) {
                            doSQLExprTableSource(sqlExprTableSource);
                            return true;
                        }

                    });
                    where.accept(new SQLASTVisitorAdapter() {
                        @Override
                        public boolean visit(SQLJoinTableSource sqlExprTableSource) {
                            doSqlJoinTableSource(sqlExprTableSource);
                            return true;
                        }

                    });
                }

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
                this.doSqlJoinTableSource(sqlJoinTableSource);
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

