package xxs.common.module.codegenerate.method.whereparam;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.enums.WhereSearchParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;
import xxs.common.module.codegenerate.model.SearchColumnInfo;

import java.util.Properties;

/**
 * @author xxs
 */
public abstract class CompareXmlWhereParamNode extends AbstractXmlWhereParamNode {
    static final String STRING_COMPARE_TEMPLATE = "<if test=\"${paramName}!=null and ${paramName}!=''\">\n" +
            "             and ${columnName} ${compareSymbol} ${compareParamValue}\n" +
            "          </if>";

    static final String OTHER_COMPARE_TEMPLATE = "<if test=\"${paramName}!=null\">\n" +
            "             and ${columnName} ${compareSymbol} ${compareParamValue}\n" +
            "          </if>";
    static final String COMPARE_PARAM_TEMPLATE_VALUE_NAME = "compareParamValue";
    static final String DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE = "#{${paramName}}";

    public CompareXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        super(whereParam, paramType);
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(PARAM_NAME_PLACEHOLDER_HELPER, "a");
        properties.put(COLUMN_NAME_PLACEHOLDER_HELPER, "b");
        properties.put(COMPARE_SYMBOL_PLACEHOLDER_HELPER, "=");
        System.out.println(CompareXmlWhereParamNode.PLACEHOLDER_HELPER.replacePlaceholders(DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE, properties));
        properties.put(COMPARE_PARAM_TEMPLATE_VALUE_NAME,CompareXmlWhereParamNode.PLACEHOLDER_HELPER.replacePlaceholders(DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE, properties) );
        String result = CompareXmlWhereParamNode.PLACEHOLDER_HELPER.replacePlaceholders(STRING_COMPARE_TEMPLATE, properties);
        System.out.println(result);
    }

    @Override
    public String getWhereParamNode() {
        Properties properties = new Properties();
        String paramName = "";
        String columnName = "";
        if (whereParam.getSearchColumnInfo() != null && whereParam.getSearchColumnInfo().getCamelCaseColumnName() != null) {
            SearchColumnInfo searchColumnInfo = whereParam.getSearchColumnInfo();
            paramName = searchColumnInfo.getCamelCaseColumnName();
            columnName = searchColumnInfo.getRealColumnName();
        } else {
            String columnName1 = whereParam.getColumnName();
            paramName = StrUtil.toCamelCase(columnName1);
            columnName = columnName1;
        }
        String template = "";
        if (WhereSearchParamType.STRING.equals(whereParam.getWhereSearchParamType())) {
            template = OTHER_COMPARE_TEMPLATE;
        } else {
            template = STRING_COMPARE_TEMPLATE;
        }
        properties.put(PARAM_NAME_PLACEHOLDER_HELPER, whereParam + paramName);
        properties.put(COLUMN_NAME_PLACEHOLDER_HELPER, columnName);
        properties.put(COMPARE_SYMBOL_PLACEHOLDER_HELPER, PLACEHOLDER_HELPER.replacePlaceholders(this.getCompareSymbol(), properties));
        properties.put(COMPARE_PARAM_TEMPLATE_VALUE_NAME, PLACEHOLDER_HELPER.replacePlaceholders(this.getCompareValueName(), properties));
        return PLACEHOLDER_HELPER.replacePlaceholders(template, properties);
    }

    protected String getCompareValueName() {
        return DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE;
    }

    protected abstract String getCompareSymbol();
}
