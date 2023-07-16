package xxs.common.module.codegenerate.method.whereparam;

import com.alibaba.druid.DbType;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.enums.WhereParamOperationType;
import xxs.common.module.codegenerate.method.model.WhereParam;

import java.util.Properties;

/**
 * @author xxs
 */
public abstract class CompareXmlWhereParamNode extends AbstractXmlWhereParamNode {
    static final String STRING_COMPARE_TEMPLATE =
            "    <if test=\"${paramName} != null and ${paramName} != ''\">\n" +
                    "       ${logicOperator} ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";

    static final String OBJECT_COMPARE_TEMPLATE =
            "    <if test=\"${paramName} != null\">\n" +
                    "        ${logicOperator} ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";
    static final String COLLECTION_COMPARE_TEMPLATE =
            "    <if test=\"${paramName} != null and ${paramName}.size() > 0\">\n" +
                    "        ${logicOperator} ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";
    static final String COMPARE_PARAM_TEMPLATE_VALUE_NAME = "compareParamValue";
    static final String DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE = "#{${paramName}}";

    static final String BEGIN_PARAM_NAME = "beginParamName";
    static final String END_PARAM_NAME = "endParamName";

    public CompareXmlWhereParamNode(DbType dbType, WhereParam whereParam, ParamType paramType) {
        super(dbType, whereParam, paramType);
    }

    @Override
    public String getWhereParamNode() {
        Properties properties = new Properties();
        String paramName = whereParam.getParamName();
        String columnName = whereParam.getColumnName();
        String template;
        template = getCompareTemplate();
        properties.put(BEGIN_PARAM_NAME, wherePre + whereParam.getBeginParamName());
        properties.put(END_PARAM_NAME, wherePre + whereParam.getEndParamName());
        properties.put(PARAM_NAME_PLACEHOLDER_HELPER, wherePre + paramName);
        properties.put(COLUMN_NAME_PLACEHOLDER_HELPER, columnName);
        properties.put(LOGIC_OPERATOR_PLACEHOLDER_HELPER, whereParam.getLogicOperator().getName());
        properties.put(COMPARE_SYMBOL_PLACEHOLDER_HELPER, Constants.PLACEHOLDER_HELPER.replacePlaceholders(this.getCompareSymbol(), properties));
        properties.put(COMPARE_PARAM_TEMPLATE_VALUE_NAME, Constants.PLACEHOLDER_HELPER.replacePlaceholders(this.getCompareValueName(), properties));
        return Constants.PLACEHOLDER_HELPER.replacePlaceholders(template, properties);
    }

    protected String getCompareTemplate() {
        String template;
        if (whereParam.getWhereParamOperationType().equals(WhereParamOperationType.IN)) {
            template = COLLECTION_COMPARE_TEMPLATE;
        } else {
            if (String.class.isAssignableFrom(whereParam.getParamType())) {
                template = STRING_COMPARE_TEMPLATE;
            } else {
                template = OBJECT_COMPARE_TEMPLATE;
            }
        }
        return template;
    }

    protected String getCompareValueName() {
        return DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE;
    }

    protected abstract String getCompareSymbol();
}
