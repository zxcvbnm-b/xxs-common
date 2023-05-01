package xxs.common.module.codegenerate.method.whereparam;

import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class BetweenXmlWhereParamNode extends CompareXmlWhereParamNode {
    static final String STRING_COMPARE_TEMPLATE_BETWEEN =
            "    <if test=\"${beginParamName} != null and ${beginParamName} != '' and ${endParamName} != null and ${endParamName} != ''\">\n" +
                    "       and ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";

    static final String OTHER_COMPARE_TEMPLATE_BETWEEN =
            "    <if test=\"${beginParamName} != null and ${endParamName} != null\">\n" +
                    "        and ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";

    public BetweenXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        super(whereParam, paramType);
    }

    @Override
    public String getCompareSymbol() {
        return "between ";
    }

    @Override
    protected String getCompareValueName() {

        return String.format("#{${beginParamName}} and #{${endParamName}}", this.wherePre + whereParam.getBeginParamName(), this.wherePre + whereParam.getEndParamName());
    }

    @Override
    protected String getCompareTemplate() {
        String template;
        if (String.class.isAssignableFrom(whereParam.getParamType())) {
            template = STRING_COMPARE_TEMPLATE_BETWEEN;
        } else {
            template = OTHER_COMPARE_TEMPLATE_BETWEEN;
        }
        return template;
    }
}
