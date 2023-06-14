package xxs.common.module.codegenerate.method.sql;

import cn.hutool.core.util.ReUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import lombok.Data;
import lombok.SneakyThrows;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.springframework.util.CollectionUtils;
import xxs.common.module.codegenerate.method.enums.WhereParamOperationType;
import xxs.common.module.codegenerate.method.model.SqlWhereExpressionOperateParam;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO 根据sql生成 dto，result，sql 思路：遍历所有的where 得到Expression expression 然后 expression.accept那些and  or 什么的语句。
 * 不支持 （column regexp xxx）=false语法
 *
 * @author issuser
 */
public class SqlDisposeUtilsV1 {
    private String currentLogicOp;
    private List<SqlWhereExpressionOperateParam> sqlWhereExpressionOperateParams = new ArrayList<>();
    private ItemsListVisitorAdapter itemsListVisitorAdapter = new ItemsListVisitorAdapter() {
        @Override
        public void visit(SubSelect subSelect) {
            processWhereSubSelectBody(subSelect);
        }
    };


    public static void main(String[] args) throws JSQLParserException {
        String customSql = "WITH\n" +
                "  cte1 AS (select 1),\n" +
                "  cte2 AS (SELECT c, d FROM table2 where id=3 group by a)\n" +
                "SELECT b, d FROM cte1 JOIN cte2\n" +
                "WHERE cte1.a = cte2.c and id = (select * from user u inner join user u2 on u.id=u.u2 where id=444 group by 1);\n";
        SqlDisposeUtilsV1 sqlDisposeUtilsV1 = new SqlDisposeUtilsV1();
        List<SqlWhereExpressionOperateParam> sqlWhereExpressionOperateParams = sqlDisposeUtilsV1.processSelectBody("select * from user join role on id = role.rid where createTime between '#{beginTime}' and '#{endTime}' and id ='#{id}' and name like '%#{name}%' and id in ('#{id2}') and name in (select 1) and id = (select * from city where cityid='#{cityid}' and a>'#{abc}' )");
        String s = null;
        for (SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam : sqlWhereExpressionOperateParams) {
            System.out.println(sqlWhereExpressionOperateParam.getFindPattern().toString());
            s = ReUtil.replaceAll(s, sqlWhereExpressionOperateParam.getFindPattern(), "aaaaa");
        }
        System.out.println(s);
    }

    /**
     * 简单验证SQL
     */
    public static boolean validSql(String checkSql, boolean isWrapTableName) {
        try {
            if (isWrapTableName) {
                checkSql = wrapTableName(checkSql, null);
            }
            CCJSqlParserUtil.parse(checkSql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 解析处理SQL
     */
    public List<SqlWhereExpressionOperateParam> processSelectBody(String selectSql) throws JSQLParserException {
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
        return sqlWhereExpressionOperateParams;
    }

    private void processSelectBody(SelectBody selectBody) throws JSQLParserException {
        if (selectBody instanceof PlainSelect) {
            parsPlainSelect(selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else if (selectBody instanceof SetOperationList) {
            SetOperationList operationList = (SetOperationList) selectBody;
            List<SelectBody> selects = operationList.getSelects();
            for (SelectBody select : selects) {
                processSelectBody(select);
            }
        }
    }

    private void parsPlainSelect(SelectBody selectBody) throws JSQLParserException {
        PlainSelect plain = (PlainSelect) selectBody;
        FromItem fromItem = plain.getFromItem();
        TableInfo tableInfo = getTableInfo(fromItem);
        visitWhereSqlBlock(plain, tableInfo);
    }

    /**
     * 获取表的别名信息 ，是否匹配输入的表名
     */
    private TableInfo getTableInfo(FromItem fromItem) throws JSQLParserException {
        TableInfo tableInfo = new TableInfo();
        if (fromItem == null) {
            return tableInfo;
        }
        String fromItemName = "";
        if (fromItem instanceof Table) {
            fromItemName = ((Table) fromItem).getName();
        }
        if (fromItem instanceof SubSelect) {
            SelectBody subSelectBody = ((SubSelect) fromItem).getSelectBody();
            processSelectBody(subSelectBody);
        }
        tableInfo.setTableName(fromItemName);
        if (fromItem.getAlias() != null) {
            tableInfo.setTableAlias(fromItem.getAlias().getName());
        }
        return tableInfo;
    }


    /**
     * TODO 处理where代码块主要逻辑
     */
    private void visitWhereSqlBlock(PlainSelect plain, TableInfo tableInfoMap) throws JSQLParserException {
        if (plain.getWhere() == null) {
            //没有where条件
        } else {
            Expression where = plain.getWhere();
            where.accept(new ExpressionVisitorAdapter() {
                private void parseLogicOperator(BinaryExpression binaryExpression, String operator) {
                    binaryExpression.getLeftExpression().accept(this);
                    currentLogicOp = operator;
                    binaryExpression.getRightExpression().accept(this);
                }

                @Override
                public void visit(EqualsTo expr) {
                    Expression rightExpression = expr.getRightExpression();
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression != null) {
                        leftExpression.accept(this);
                    }
                    if (rightExpression != null) {
                        rightExpression.accept(this);
                    }
                    SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = SqlWhereExpressionItemParseUtils.parseCommonCompareType(expr.toString(), leftExpression, rightExpression, WhereParamOperationType.EQ, tableInfoMap.getTableName(), tableInfoMap.getTableAlias(), currentLogicOp);
                    if (sqlWhereExpressionOperateParam != null) {
                        sqlWhereExpressionOperateParams.add(sqlWhereExpressionOperateParam);
                        currentLogicOp = "";
                    }
                }

                //TODO  其他也要如此处理子查询 不然遍历不到子查询中得{111}占位符
                @Override
                public void visit(InExpression expr) {
                    ItemsList rightItemsList = expr.getRightItemsList();
                    if (expr.getRightItemsList() != null) {
                        if (rightItemsList instanceof ExpressionList) {
                            ExpressionList expressionList = (ExpressionList) rightItemsList;
                            SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = SqlWhereExpressionItemParseUtils.parseInCompareType(expr.toString(), expr.getLeftExpression(), expressionList, WhereParamOperationType.IN, tableInfoMap.getTableName(), tableInfoMap.getTableAlias(), currentLogicOp);
                            if (sqlWhereExpressionOperateParam != null) {
                                sqlWhereExpressionOperateParams.add(sqlWhereExpressionOperateParam);
                                currentLogicOp = "";
                            }
                        }
                        expr.getRightItemsList().accept(itemsListVisitorAdapter);
                    }
                }

                @Override
                public void visit(AndExpression expr) {
                    Expression rightExpression = expr.getRightExpression();
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression != null) {
                        leftExpression.accept(this);
                    }
                    if (rightExpression != null) {
                        rightExpression.accept(this);
                    }
                    parseLogicOperator(expr, WhereParamOperationType.AND.getName());
                }

                @Override
                public void visit(OrExpression expr) {
                    Expression rightExpression = expr.getRightExpression();
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression != null) {
                        leftExpression.accept(this);
                    }
                    if (rightExpression != null) {
                        rightExpression.accept(this);
                    }
                    parseLogicOperator(expr, WhereParamOperationType.OR.getName());

                }

                @Override
                public void visit(Between expr) {
                    if (expr.getLeftExpression() != null) {
                        expr.getLeftExpression().accept(this);
                    }
                    if (expr.getBetweenExpressionEnd() != null) {
                        expr.getBetweenExpressionEnd().accept(this);
                    }
                    if (expr.getBetweenExpressionStart() != null) {
                        expr.getBetweenExpressionStart().accept(this);
                    }
                    SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = SqlWhereExpressionItemParseUtils.parseBetweenCompareType(expr, tableInfoMap.getTableName(), tableInfoMap.getTableAlias(), currentLogicOp);
                    if (sqlWhereExpressionOperateParam != null) {
                        sqlWhereExpressionOperateParams.add(sqlWhereExpressionOperateParam);
                        currentLogicOp = "";
                    }

                }

                @Override
                public void visit(GreaterThan expr) {
                    Expression rightExpression = expr.getRightExpression();
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression != null) {
                        leftExpression.accept(this);
                    }
                    if (rightExpression != null) {
                        rightExpression.accept(this);
                    }
                    SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = SqlWhereExpressionItemParseUtils.parseCommonCompareType(expr.toString(), leftExpression, rightExpression, WhereParamOperationType.GT, tableInfoMap.getTableName(), tableInfoMap.getTableAlias(), currentLogicOp);
                    if (sqlWhereExpressionOperateParam != null) {
                        sqlWhereExpressionOperateParams.add(sqlWhereExpressionOperateParam);
                        currentLogicOp = "";
                    }

                }

                @Override
                public void visit(GreaterThanEquals expr) {
                    Expression rightExpression = expr.getRightExpression();
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression != null) {
                        leftExpression.accept(this);
                    }
                    if (rightExpression != null) {
                        rightExpression.accept(this);
                    }
                    SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = SqlWhereExpressionItemParseUtils.parseCommonCompareType(expr.toString(), leftExpression, rightExpression, WhereParamOperationType.GE, tableInfoMap.getTableName(), tableInfoMap.getTableAlias(), currentLogicOp);
                    if (sqlWhereExpressionOperateParam != null) {
                        sqlWhereExpressionOperateParams.add(sqlWhereExpressionOperateParam);
                        currentLogicOp = "";
                    }

                }

                @Override
                public void visit(LikeExpression expr) {
                    Expression rightExpression = expr.getRightExpression();
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression != null) {
                        leftExpression.accept(this);
                    }
                    if (rightExpression != null) {
                        rightExpression.accept(this);
                    }
                    SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = SqlWhereExpressionItemParseUtils.parseCommonCompareType(expr.toString(), leftExpression, rightExpression, WhereParamOperationType.LIKE, tableInfoMap.getTableName(), tableInfoMap.getTableAlias(), currentLogicOp);
                    if (sqlWhereExpressionOperateParam != null) {
                        sqlWhereExpressionOperateParams.add(sqlWhereExpressionOperateParam);
                        currentLogicOp = "";
                    }
                }

                @Override
                public void visit(NotEqualsTo expr) {
                    Expression rightExpression = expr.getRightExpression();
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression != null) {
                        leftExpression.accept(this);
                    }
                    if (rightExpression != null) {
                        rightExpression.accept(this);
                    }
                    SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = SqlWhereExpressionItemParseUtils.parseCommonCompareType(expr.toString(), leftExpression, rightExpression, WhereParamOperationType.NEQ, tableInfoMap.getTableName(), tableInfoMap.getTableAlias(), currentLogicOp);
                    if (sqlWhereExpressionOperateParam != null) {
                        sqlWhereExpressionOperateParams.add(sqlWhereExpressionOperateParam);
                        currentLogicOp = "";
                    }

                }

                @Override
                public void visit(RegExpMatchOperator expr) {
                    Expression rightExpression = expr.getRightExpression();
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression != null) {
                        leftExpression.accept(this);
                    }
                    if (rightExpression != null) {
                        rightExpression.accept(this);
                    }
                    SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = SqlWhereExpressionItemParseUtils.parseCommonCompareType(expr.toString(), leftExpression, rightExpression, WhereParamOperationType.LIKE, tableInfoMap.getTableName(), tableInfoMap.getTableAlias(), currentLogicOp);
                    if (sqlWhereExpressionOperateParam != null) {
                        sqlWhereExpressionOperateParams.add(sqlWhereExpressionOperateParam);
                        currentLogicOp = "";
                    }

                }

                @Override
                public void visit(SubSelect subSelect) {
                    processWhereSubSelectBody(subSelect);
                }
            });
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

    @Data
    static class TableInfo {
        private String tableName;
        private String tableAlias;
    }
}
