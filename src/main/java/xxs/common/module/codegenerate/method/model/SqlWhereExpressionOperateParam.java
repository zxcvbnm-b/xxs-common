package xxs.common.module.codegenerate.method.model;

import cn.hutool.core.util.ReUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import xxs.common.module.codegenerate.TypeMapperRegistry;
import xxs.common.module.codegenerate.enums.LogicOperator;
import xxs.common.module.codegenerate.enums.WhereParamOperationType;

import java.util.regex.Pattern;

/**
 * SQL where参数信息 比如解析 select * from user where id = '{id}' 得出---- id这个参数得所有信息转换成mybatis得语法。
 *
 * @author xxs
 */
@Data
public class SqlWhereExpressionOperateParam {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表别名
     */
    private String tableAlias;
    /**
     * 列名
     */
    private String columnName;
    /**
     * jdbc类型名称--根据表名查询数据库得出--默认String
     */
    private String columnJdbcTypeName = "VARCHAR";
    /**
     * java类型--根据表名查询数据库得出--默认String
     */
    private Class columnJavaType = TypeMapperRegistry.getJavaType(12);
    /**
     * jdbc类型code--根据表名查询数据库得出--默认String
     */
    private int columnJdbcTypeCode = 12;
    /**
     * 参数名（'#{name}'）得到这个名称
     */
    private String whereParamName;

    /**
     * where条件操作类型
     */
    private WhereParamOperationType sqlWhereParamType;
    /**
     * between开始参数名
     */
    private String beginParamName;
    /**
     * between结束参数名 --用来解析完成之后再统一得进行替换即可
     */
    private String endParamName;

    /**
     * and or or
     */
    private LogicOperator logicOperator;

    /**
     * 表达式 比如 id = 1 （但是需要注意得是这个表达式会被格式化的）
     */
    private String expression;
    /**
     * left 表达式
     */
    private String leftExpression;
    /**
     * right 表达式
     */
    private String rightExpression;

    /**
     * begin 表达式--between 会有
     */
    private String beginExpression;
    /**
     * end 表达式--between 会有
     */
    private String endExpression;

    /**
     * 获取字符串匹配替换表达式 （因为在进行AST遍历的时候表达式会被格式化，所以不能直接通过expression进行替换字符串你）
     *
     * @return
     */
    public Pattern getFindPattern() {
        StringBuilder patternStringBuilder = new StringBuilder();
        if (logicOperator != null) {
            patternStringBuilder.append("\\s+");
            patternStringBuilder.append(logicOperator.getName());
            patternStringBuilder.append("\\s+");
        }
        patternStringBuilder.append(leftExpression);
        patternStringBuilder.append("\\s*");
        patternStringBuilder.append(sqlWhereParamType.getName());
        patternStringBuilder.append("\\s*");
        if (StringUtils.isNotEmpty(rightExpression)) {
            patternStringBuilder.append(ReUtil.escape(rightExpression));
        }
        if (WhereParamOperationType.BETWEEN.equals(sqlWhereParamType)) {
            if (StringUtils.isNotEmpty(beginExpression)) {
                patternStringBuilder.append("\\s*");
                patternStringBuilder.append(ReUtil.escape(beginExpression));
            }
            patternStringBuilder.append("\\s*");
            patternStringBuilder.append("and");
            if (StringUtils.isNotEmpty(endExpression)) {
                patternStringBuilder.append("\\s*");
                patternStringBuilder.append(ReUtil.escape(endExpression));
            }
        }
        //忽略大小写
        Pattern pattern = Pattern.compile(patternStringBuilder.toString(), Pattern.CASE_INSENSITIVE);
        return pattern;
    }
}
