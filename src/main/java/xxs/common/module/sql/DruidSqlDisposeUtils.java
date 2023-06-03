package xxs.common.module.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;
import xxs.common.module.codegenerate.Constants;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 给where添加条件
 *
 * @author xxs
 */
public class DruidSqlDisposeUtils {
    public static void main(String[] args) {
        String sql = "  select  phone from 1shujuzhiliang01 a inner join  1shujuzhiliang01 b on a.a=b.b  inner join  1shujuzhiliang01 c on c.a=b.b  where  (phone regexp '^1([3578][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$')= false and id in (select * from 1shujuzhiliang01 aa inner join 1shujuzhiliang01 bb on aa.a=bb.b)";
        String sql2 = "with test_with as(select * from 1shujuzhiliang1 a inner join shujuzhiliang b on a.a=b.b inner join shujuzhiliang c on c.a=c.c  ) select * from B inner join shujuzhiliang A on A.a=B.b  inner join shujuzhiliang C on C.a=B.b  inner join (select * from shujuzhiliang E inner join shujuzhiliang F on F.a=E.a) D on D.a=B.b where " +
                "id in (select * from shujuzhiliang z inner join shujuzhiliang q on z.id=q.id)";
        String sql3 = "select * from student2 a where id < 4\n" +
                "\n" +
                "union all\n" +
                "\n" +
                "select * from student2 where id > 2 and id < 6";
        String result = DruidSqlDisposeUtils.processCheckSqlWhere(sql, "a=1 or b=1 ", "1shujuzhiliang01", JdbcConstants.HIVE.name());

        getTableAliasMap(sql3, JdbcConstants.MYSQL.name());

        Map<String, String> map = new HashMap<>();
        map.put("student2", " ${tableAlias}.columnName,${tableAlias}.columnName");
        String s = replaceSqlProjectionByTableAlias(sql3, JdbcConstants.MYSQL.name(), map);
        System.out.println(s);
        System.out.println(hasFirstQueryBlockWhere("select * from user where id=1 ",JdbcConstants.MYSQL.name()));
    }

    /**
     * 逻辑：从sql中获取到最外一层表的信息，拿到表别名，然后拼接到投影返回列上，比如 select * from table -> select id,name from table 对于union也进行了sql处理
     *
     * @param sql                    需要处理的sql
     * @param dbType                 数据库类型
     * @param tableNameProjectionMap key table ,value 表的占位符字符串如 ${alias}.columnName,${alias}.columnName
     * @return 处理完成之后的sql
     */
    public static String replaceSqlProjectionByTableAlias(String sql, String dbType, Map<String, String> tableNameProjectionMap) {
        List<String> projectionStringList = new ArrayList<>();
        //1.获取到所有表的表名和表的别名的映射关系
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
        List<Map<String, String>> tableAliasMapList = getTableAliasMap(sql, dbType);
        for (Map.Entry<String, String> tableNameProjectionEntity : tableNameProjectionMap.entrySet()) {
            String tableName = tableNameProjectionEntity.getKey();
            String projectionColumns = tableNameProjectionEntity.getValue();
            String currTableAlias = "";
            Iterator<Map<String, String>> iterator = tableAliasMapList.iterator();
            tableAliasMap:
            while (iterator.hasNext()) {
                Map<String, String> tableAliasItem = iterator.next();
                String alias = tableAliasItem.get(tableName);
                currTableAlias = alias;
                if (!StringUtils.isEmpty(alias)) {
                    //当这个表别名被使用使用一次的时候删除掉
                    iterator.remove();
                    break tableAliasMap;
                }
            }
            String finalCurrTableAlias = currTableAlias;
            String tableAlias = Constants.PLACEHOLDER_HELPER.replacePlaceholders(projectionColumns, (placeholderName) -> {
                if (Constants.TABLE_ALIAS_PLACEHOLDER.equals(placeholderName)) {
                    return finalCurrTableAlias;
                }
                return null;
            });
            projectionStringList.add(tableAlias);
        }

        String projectionString = projectionStringList.stream().collect(Collectors.joining("\n" + Constants.COMMA_SEPARATOR));
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
        SQLSelect sqlSelect = sqlSelectStatement.getSelect();
        // 如果是union也处理
        if (sqlSelect.getQuery() instanceof SQLUnionQuery) {
            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelect.getQuery();
            //如果是union
            List<SQLSelectQuery> sqlSelectQueries = sqlUnionQuery.getRelations();
            for (SQLSelectQuery sqlSelectQuery : sqlSelectQueries) {
                if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
                    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
                    // 暂不支持union语法的 setSelectList(projectionString, queryBlock);
                }
            }
        } else {
            SQLSelectQueryBlock queryBlock = sqlSelect.getQueryBlock();
            setSelectList(projectionString, queryBlock);
        }
        return SQLUtils.format(SQLUtils.toSQLString(sqlSelect, dbType), dbType);
    }

    public static void setSelectList(String projectionString, SQLSelectQueryBlock firstQueryBlock) {
        //清除投影列信息
        firstQueryBlock.getSelectList().clear();
        SQLSelectItem sqlSelectItem = new SQLSelectItem();
        SQLIdentifierExpr sqlIdentifierExpr = new SQLIdentifierExpr();
        sqlIdentifierExpr.setName(projectionString.toUpperCase(Locale.ROOT));
        //添加投影列信息
        sqlSelectItem.setExpr(sqlIdentifierExpr);
        firstQueryBlock.getSelectList().add(sqlSelectItem);
    }

    /**
     * 给查询sql添加limit 1
     *
     * @param sql
     * @param dbType
     */
    public static String setSelectLimit(String sql, String dbType) {
        return setSelectLimit(sql, dbType, 0, 1);
    }

    /**
     * 给查询sql添加limit
     *
     * @param sql
     * @param dbType
     */
    public static String setSelectLimit(String sql, String dbType, Integer rowCount, Integer offset) {
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
        SQLSelect select = sqlSelectStatement.getSelect();
        SQLSelectQueryBlock firstQueryBlock = select.getFirstQueryBlock();
        SQLLimit limit1 = firstQueryBlock.getLimit();
        if (limit1 == null) {
            SQLLimit limit = new SQLLimit();
            limit.setRowCount(rowCount);
            limit.setOffset(offset);
            firstQueryBlock.setLimit(limit);
        }
        return SQLUtils.toSQLString(select, dbType);
    }

    /**
     * 判断这个sql最外层是否有where条件
     *
     * @param sql
     * @param dbType
     * @return
     */
    public static boolean hasFirstQueryBlockWhere(String sql, String dbType) {
        SQLSelectQueryBlock firstQueryBlock = getSqlFirstSelectQueryBlock(sql, dbType);
        SQLExpr where = firstQueryBlock.getWhere();
        return where != null;
    }

    /**
     * get firstQueryBlock
     *
     * @param sql
     * @param dbType
     * @return
     */
    private static SQLSelectQueryBlock getSqlFirstSelectQueryBlock(String sql, String dbType) {
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
        SQLSelect select = sqlSelectStatement.getSelect();
        SQLSelectQueryBlock firstQueryBlock = select.getFirstQueryBlock();
        return firstQueryBlock;
    }

    /**
     * 给查询sql添加limit 1
     *
     * @param sql
     */
    public static String setSelectLimit(String sql) {
        return setSelectLimit(sql, JdbcConstants.MYSQL.name());
    }

    /**
     * 使用list是因为可能有多个表名 但是别名不一样。。。
     *
     * @return
     */
    public static List<Map<String, String>> getTableAliasMap(String sql, String dbType) {
        List<Map<String, String>> resultList = new ArrayList<>();
        //1.获取到所有表的表明和表的别名的映射关系
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
        sqlStatement.accept(new SQLASTVisitorAdapter() {
            @Override
            public boolean visit(SQLExprTableSource x) {
                Map<String, String> tableNameAliasMap = new HashMap<>();
                SQLName tableName = x.getName();
                String tableAlias = x.getAlias();
                String simpleTableName = tableName.getSimpleName();
                if (StringUtils.isEmpty(tableAlias)) {
                    tableAlias = simpleTableName;
                }
                tableNameAliasMap.put(simpleTableName, tableAlias);
                resultList.add(tableNameAliasMap);
                return true;
            }
        });
        return resultList;
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

