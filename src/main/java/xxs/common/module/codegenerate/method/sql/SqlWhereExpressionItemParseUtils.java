package xxs.common.module.codegenerate.method.sql;

import cn.hutool.core.collection.CollectionUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import org.apache.commons.lang3.StringUtils;
import xxs.common.module.codegenerate.cache.TableInfoTemCache;
import xxs.common.module.codegenerate.method.enums.LogicOperator;
import xxs.common.module.codegenerate.method.enums.WhereParamOperationType;
import xxs.common.module.codegenerate.method.model.SqlWhereExpressionOperateParam;
import xxs.common.module.codegenerate.model.ColumnInfo;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.utils.other.PlaceholderStringResolver;

import java.sql.SQLException;
import java.util.List;

/**
 * sql where 表达式项解析工具
 *
 * @author xxs
 */
public class SqlWhereExpressionItemParseUtils {
    private TableInfoTemCache tableInfoTemCache;

    public SqlWhereExpressionItemParseUtils(TableInfoTemCache tableInfoTemCache) {
        this.tableInfoTemCache = tableInfoTemCache;
    }

    /**
     * TODO sqlWhereExpressionOperateParams 二次处理参数，将列信息封装到SqlWhereExpressionOperateParam，用于后续使用
     * 初始化列的类型信息 （需要查询数据库）
     *
     * @param sqlWhereExpressionOperateParams
     * @return
     * @throws SQLException
     */
    public void initSqlWhereExpressionOperateParamColumnInfo(List<SqlWhereExpressionOperateParam> sqlWhereExpressionOperateParams) throws Exception {
        if (CollectionUtil.isNotEmpty(sqlWhereExpressionOperateParams)) {
            for (SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam : sqlWhereExpressionOperateParams) {
                String tableName = sqlWhereExpressionOperateParam.getTableName();
                if (StringUtils.isNotEmpty(tableName)) {
                    TableInfo tableInfo = tableInfoTemCache.getTableInfo(tableName);
                    if (tableInfo != null) {
                        ColumnInfo columnInfo = tableInfo.getColumnInfoByColumnName(sqlWhereExpressionOperateParam.getColumnName());
                        if (columnInfo != null) {
                            sqlWhereExpressionOperateParam.setColumnJavaType(columnInfo.getJavaType());
                            sqlWhereExpressionOperateParam.setColumnJdbcTypeCode(columnInfo.getJdbcTypeCode());
                            sqlWhereExpressionOperateParam.setColumnJdbcTypeName(columnInfo.getJdbcTypeName());
                        }
                    }
                }
            }
        }
    }

    /**
     * 解析常见的in类型的sql代码块
     *
     * @param leftExpression
     * @param rightExpression
     * @param whereParamOperationType
     */
    public static SqlWhereExpressionOperateParam parseInCompareType(String expression, Expression leftExpression, ExpressionList rightExpression, WhereParamOperationType whereParamOperationType, String tableName, String tableAlias, LogicOperator currentLogicOperator) {
        if (leftExpression == null || rightExpression == null || CollectionUtil.isEmpty(rightExpression.getExpressions())) {
            return null;
        }
        Expression expressionItem = rightExpression.getExpressions().get(0);
        if (!(expressionItem instanceof StringValue)) {
            return null;
        }
        List<String> placeholderString = PlaceholderStringResolver.getPlaceholderStrings(rightExpression.toString());
        if (!CollectionUtil.isEmpty(placeholderString)) {
            SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = new SqlWhereExpressionOperateParam();
            sqlWhereExpressionOperateParam.setSqlWhereParamType(whereParamOperationType);
            sqlWhereExpressionOperateParam.setWhereParamName(placeholderString.get(0));
            sqlWhereExpressionOperateParam.setTableName(tableName);
            sqlWhereExpressionOperateParam.setTableAlias(tableAlias);
            sqlWhereExpressionOperateParam.setExpression(expression);
            sqlWhereExpressionOperateParam.setLogicOperator(currentLogicOperator);
            sqlWhereExpressionOperateParam.setLeftExpression(leftExpression.toString());
            sqlWhereExpressionOperateParam.setRightExpression(rightExpression.toString());
            sqlWhereExpressionOperateParam.setColumnName(leftExpression.toString());
            return sqlWhereExpressionOperateParam;
        }
        return null;
    }

    /**
     * 解析常见的比较类型sql代码块，比如 and  or等
     *
     * @param leftExpression
     * @param rightExpression
     * @param whereParamOperationType
     */
    public static SqlWhereExpressionOperateParam parseCommonCompareType(String expression, Expression leftExpression, Expression rightExpression, WhereParamOperationType whereParamOperationType, String tableName, String tableAlias, LogicOperator currentLogicOperator) {
        if (leftExpression == null || rightExpression == null) {
            return null;
        }
        if (!(rightExpression instanceof StringValue)) {
            return null;
        }
        List<String> placeholderString = PlaceholderStringResolver.getPlaceholderStrings(rightExpression.toString());
        if (!CollectionUtil.isEmpty(placeholderString)) {
            SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = new SqlWhereExpressionOperateParam();
            sqlWhereExpressionOperateParam.setSqlWhereParamType(whereParamOperationType);
            sqlWhereExpressionOperateParam.setWhereParamName(placeholderString.get(0));
            sqlWhereExpressionOperateParam.setTableName(tableName);
            sqlWhereExpressionOperateParam.setTableAlias(tableAlias);
            sqlWhereExpressionOperateParam.setExpression(expression);
            sqlWhereExpressionOperateParam.setLogicOperator(currentLogicOperator);
            sqlWhereExpressionOperateParam.setLeftExpression(leftExpression.toString());
            sqlWhereExpressionOperateParam.setRightExpression(rightExpression.toString());
            sqlWhereExpressionOperateParam.setColumnName(leftExpression.toString());
            return sqlWhereExpressionOperateParam;
        }
        return null;
    }

    /**
     * 解析between比较类型sql代码块
     */
    public static SqlWhereExpressionOperateParam parseBetweenCompareType(Between between, String tableName, String tableAlias, LogicOperator currentLogicOperator) {
        if (between == null) {
            return null;
        }
        String expression = between.toString();
        if (PlaceholderStringResolver.hasPlaceholderString(between.getBetweenExpressionStart().toString()) || PlaceholderStringResolver.hasPlaceholderString(between.getBetweenExpressionEnd().toString())) {
            SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam = new SqlWhereExpressionOperateParam();
            sqlWhereExpressionOperateParam.setSqlWhereParamType(WhereParamOperationType.BETWEEN);
            sqlWhereExpressionOperateParam.setTableName(tableName);
            sqlWhereExpressionOperateParam.setTableAlias(tableAlias);
            sqlWhereExpressionOperateParam.setExpression(expression);
            sqlWhereExpressionOperateParam.setLogicOperator(currentLogicOperator);
            sqlWhereExpressionOperateParam.setColumnName(between.getLeftExpression().toString());
            Expression betweenExpressionStart = between.getBetweenExpressionStart();
            sqlWhereExpressionOperateParam.setLeftExpression(between.getLeftExpression().toString());
            if (betweenExpressionStart != null) {
                List<String> placeholderBetweenExpression = PlaceholderStringResolver.getPlaceholderStrings(betweenExpressionStart.toString());
                if (CollectionUtil.isNotEmpty(placeholderBetweenExpression)) {
                    sqlWhereExpressionOperateParam.setBeginParamName(placeholderBetweenExpression.get(0));
                }
                sqlWhereExpressionOperateParam.setBeginExpression(betweenExpressionStart.toString());
            }

            Expression betweenExpressionEnd = between.getBetweenExpressionEnd();
            if (betweenExpressionEnd != null) {
                List<String> placeholderBetweenExpressionEnd = PlaceholderStringResolver.getPlaceholderStrings(betweenExpressionEnd.toString());
                if (CollectionUtil.isNotEmpty(placeholderBetweenExpressionEnd)) {
                    sqlWhereExpressionOperateParam.setEndParamName(placeholderBetweenExpressionEnd.get(0));
                }
                sqlWhereExpressionOperateParam.setEndExpression(betweenExpressionEnd.toString());
            }
            return sqlWhereExpressionOperateParam;
        }
        return null;
    }

}
