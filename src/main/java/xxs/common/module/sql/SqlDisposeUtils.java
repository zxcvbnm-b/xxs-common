package xxs.common.module.sql;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import lombok.SneakyThrows;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql解析 动态的给某一个表添加某一些where 条件。
 *
 * @author issuser
 */
public class SqlDisposeUtils {
    public static void main(String[] args) throws JSQLParserException {
        String customSql = "WITH\n" +
                "  cte1 AS (select 1),\n" +
                "  cte2 AS (SELECT c, d FROM user where id=3 group by a)\n" +
                "SELECT b, d FROM cte1 JOIN cte2\n" +
                "WHERE cte1.a = cte2.c and id = (select * from user u inner join user u2 on u.id=u.u2 group by 1);\n";
        String customSql2 = "select * from user a inner join user b on a.a=b.b where id in (select * from user c)";
        String result = processCheckSqlWhere(customSql2, "(del_flag=1)", "user");
        System.out.println(result);
        boolean b = validSql(customSql);
        System.out.println(b);
    }

    /**
     * 添加where条件
     */
    public static String processCheckSqlWhere(String checkSql, String whereSqlBlock, String tableName) throws JSQLParserException {
        if (StringUtils.isEmpty(whereSqlBlock) || StringUtils.isEmpty(checkSql) || StringUtils.isEmpty(tableName)) {
            return checkSql;
        }
        checkSql = wrapTableName(checkSql, null);
        SelectDisposeWhereBlock selectDisposeWhereBlock = new SelectDisposeWhereBlock(checkSql, whereSqlBlock, tableName);
        return selectDisposeWhereBlock.processSelectBody();
    }

    /**
     * 简单验证SQL
     */
    public static boolean validSql(String checkSql) {
        try {
            checkSql = wrapTableName(checkSql, null);
            CCJSqlParserUtil.parse(checkSql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 简单验证WHERE
     */
    public static boolean validWhereSqlBlock(String whereSqlBlock) {
        try {
            CCJSqlParserUtil.parseCondExpression(whereSqlBlock);
        } catch (JSQLParserException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static class SelectDisposeWhereBlock {
        private static final String TABLE_ALIAS_KEY_NAME = "ALIAS";
        private static final String TABLE_NAME_MATCH_FLAG_VALUE = "MATCH";
        private static final String TABLE_NAME_MATCH_KEY_NAME = "TABLE_NAME_MATCH_KEY_NAME";
        private static final String TABLE_NAME_NO_MATCH_FLAG_VALUE = "NO";
        private String tableName;
        private String whereSqlBlock;
        private String selectSql;

        public SelectDisposeWhereBlock(String selectSql, String whereSqlBlock, String tableName) {
            this.selectSql = selectSql;
            this.whereSqlBlock = whereSqlBlock;
            this.tableName = tableName;
        }

        /**
         * 解析处理SQL
         */
        public String processSelectBody() throws JSQLParserException {
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            StringReader stringReader = new StringReader(selectSql);
            Select select = (Select) parserManager.parse(stringReader);
            //处理主体select
            SelectBody selectBody = select.getSelectBody();
            processSelectBody(selectBody);
            //处理with 代码块
            List<WithItem> withItemsList = select.getWithItemsList();
            if (!CollectionUtils.isEmpty(withItemsList)) {
                for (WithItem withItem : withItemsList) {
                    processSelectBody(withItem.getSelectBody());
                }
            }
            return select.toString();
        }

        public void processSelectBody(SelectBody selectBody) throws JSQLParserException {
            if (selectBody instanceof PlainSelect) {
                PlainSelect plainSelect = (PlainSelect) selectBody;
                Expression where = plainSelect.getWhere();
                if (where != null) {
                    //处理where后面的子查询
                    processWhereSubSelectBody(where);
                }
                parsPlainSelect(selectBody);
            } else if (selectBody instanceof WithItem) {
                WithItem withItem = (WithItem) selectBody;
                if (withItem.getSelectBody() != null) {
                    processSelectBody(withItem.getSelectBody());
                }
            } else {
                SetOperationList operationList = (SetOperationList) selectBody;
                List<SelectBody> selects = operationList.getSelects();
                for (SelectBody select : selects) {
                    processSelectBody(select);
                }
            }
        }

        public void parsPlainSelect(SelectBody selectBody) throws JSQLParserException {
            PlainSelect plain = (PlainSelect) selectBody;
            FromItem fromItem = plain.getFromItem();
            Map<String, String> tableInfoMap = getTableInfo(fromItem);
            if (TABLE_NAME_MATCH_FLAG_VALUE.equals(tableInfoMap.get(TABLE_NAME_MATCH_KEY_NAME))) {
                addWhereSqlBlock(plain, tableInfoMap.get(TABLE_ALIAS_KEY_NAME));
            }
            //处理连接查询
            List<Join> joinList = plain.getJoins();
            if (joinList != null) {
                for (Join join : joinList) {
                    Map<String, String> tableInfoMap1 = getTableInfo(join.getRightItem());
                    if (TABLE_NAME_MATCH_FLAG_VALUE.equals(tableInfoMap1.get(TABLE_NAME_MATCH_KEY_NAME))) {
                        addWhereSqlBlock(plain, tableInfoMap1.get(TABLE_ALIAS_KEY_NAME));
                        return;
                    }
                }
            }
        }

        /**
         * 获取表的别名信息 ，是否匹配输入的表名
         */
        public Map<String, String> getTableInfo(FromItem fromItem) throws JSQLParserException {
            Map<String, String> tableInfoMap = new HashMap<>();
            tableInfoMap.put(TABLE_NAME_MATCH_KEY_NAME, TABLE_NAME_NO_MATCH_FLAG_VALUE);
            tableInfoMap.put(TABLE_ALIAS_KEY_NAME, null);
            String fromItemName = "";
            if (fromItem instanceof Table) {
                fromItemName = ((Table) fromItem).getName();
            }
            if (fromItem instanceof SubSelect) {
                SelectBody subSelectBody = ((SubSelect) fromItem).getSelectBody();
                processSelectBody(subSelectBody);
            }
            if (fromItemName.equals(String.format("%s", tableName)) || fromItemName.equals(String.format("`%s`", tableName))) {
                tableInfoMap.put(TABLE_NAME_MATCH_KEY_NAME, TABLE_NAME_MATCH_FLAG_VALUE);
                if (fromItem.getAlias() != null) {
                    tableInfoMap.put(TABLE_ALIAS_KEY_NAME, fromItem.getAlias().getName());
                }
                return tableInfoMap;
            }
            return tableInfoMap;
        }

        /**
         * 添加where代码块
         */
        public void addWhereSqlBlock(PlainSelect plain, String alias) throws JSQLParserException {
            final Expression envCondition = CCJSqlParserUtil.parseCondExpression(whereSqlBlock);
            envCondition.accept(new ExpressionVisitorAdapter() {
                //处理别名 给where条件的属性添加别名，如果有
                @Override
                public void visit(Column expr) {
                    expr.setTable(new Table(alias));
                }
            });
            if (plain.getWhere() == null) {
                plain.setWhere(envCondition);
            } else {
                plain.setWhere(new AndExpression(plain.getWhere(), envCondition));
            }
        }

        //递归处理where表达式，子查询，包括 in 后面的子查询，> < =等 ,例：id in (select 1)
        public void processWhereSubSelectBody(Expression where) {
            where.accept(new ExpressionVisitorAdapter() {
                @SneakyThrows
                @Override
                public void visit(SubSelect subSelect) {
                    processSelectBody(subSelect.getSelectBody());
                }
            });
        }
    }

    /*替换表名: user => `user`(jsql不支持数字开头的表名)*/
    public static String wrapTableName(String sql, String dbType) {
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (SQLStatement stmt : statements) {
            stmt.accept(new SQLASTVisitorAdapter() {
                @Override
                public boolean visit(SQLExprTableSource x) {
                    String originTableName = x.getExpr().toString();
                    if (!originTableName.contains("`")) {
                        String newTableName = String.format("`%s`", originTableName);
                        if (newTableName != null) {
                            x.setExpr(new SQLIdentifierExpr(newTableName));
                        }
                    }
                    return true;
                }
            });
        }
        return SQLUtils.toSQLString(statements, JdbcConstants.HIVE);
    }
}



