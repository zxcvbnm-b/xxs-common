package xxs.common.module.codegenerate.method.whereparam;

import com.alibaba.druid.DbType;
import xxs.common.module.codegenerate.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class BetweenXmlWhereParamNode extends CompareXmlWhereParamNode {
    static final String STRING_COMPARE_TEMPLATE_BETWEEN =
            "    <if test=\"${beginParamName} != null and ${beginParamName} != '' and ${endParamName} != null and ${endParamName} != ''\">\n" +
                    "       ${logicOperator} ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";

    static final String OBJECT_COMPARE_TEMPLATE_BETWEEN =
            "    <if test=\"${beginParamName} != null and ${endParamName} != null\">\n" +
                    "        ${logicOperator} ${columnName} ${compareSymbol} ${compareParamValue}\n" +
                    "    </if>";

    public BetweenXmlWhereParamNode(DbType dbType, WhereParam whereParam, ParamType paramType) {
        super(dbType, whereParam, paramType);
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
        String template = OBJECT_COMPARE_TEMPLATE_BETWEEN;
        if (String.class.isAssignableFrom(whereParam.getParamType())) {
            template = STRING_COMPARE_TEMPLATE_BETWEEN;
        }
        return template;
    }
}
