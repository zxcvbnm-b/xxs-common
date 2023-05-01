package xxs.common.module.codegenerate.method.whereparam;

import cn.hutool.core.util.StrUtil;
import com.sun.org.apache.regexp.internal.RE;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;
import xxs.common.module.codegenerate.model.SearchColumnInfo;

import java.util.Properties;

/**
 * @author xxs
 */
public abstract class CompareXmlWhereParamNode extends AbstractXmlWhereParamNode {
    static final String STRING_COMPARE_TEMPLATE =
            "    <if test=\"${paramName} != null and ${paramName} != ''\">\n" +
                    "       and ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";

    static final String OTHER_COMPARE_TEMPLATE =
            "    <if test=\"${paramName} != null\">\n" +
                    "        and ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";
    static final String COMPARE_PARAM_TEMPLATE_VALUE_NAME = "compareParamValue";
    static final String DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE = "#{${paramName}}";

    static final String BEGIN_PARAM_NAME = "beginParamName";
    static final String END_PARAM_NAME = "endParamName";

    public CompareXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        super(whereParam, paramType);
    }

    @Override
    public String getWhereParamNode() {
        Properties properties = new Properties();
        String paramName = whereParam.getParamName();
        String columnName = "";
        String columnName1 = whereParam.getColumnName();
        columnName = columnName1;
        String template = "";
        template = getCompareTemplate();
        properties.put(BEGIN_PARAM_NAME, wherePre + whereParam.getBeginParamName());
        properties.put(END_PARAM_NAME, wherePre + whereParam.getEndParamName());
        properties.put(PARAM_NAME_PLACEHOLDER_HELPER, wherePre + paramName);
        properties.put(COLUMN_NAME_PLACEHOLDER_HELPER, columnName);
        properties.put(COMPARE_SYMBOL_PLACEHOLDER_HELPER, Constants.PLACEHOLDER_HELPER.replacePlaceholders(this.getCompareSymbol(), properties));
        properties.put(COMPARE_PARAM_TEMPLATE_VALUE_NAME, Constants.PLACEHOLDER_HELPER.replacePlaceholders(this.getCompareValueName(), properties));
        return Constants.PLACEHOLDER_HELPER.replacePlaceholders(template, properties);
    }

    protected String getCompareTemplate() {
        String template;
        if (String.class.isAssignableFrom(whereParam.getParamType())) {
            template = STRING_COMPARE_TEMPLATE;
        } else {
            template = OTHER_COMPARE_TEMPLATE;
        }
        return template;
    }

    protected String getCompareValueName() {
        return DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE;
    }

    protected abstract String getCompareSymbol();
}
