package xxs.common.module.codegenerate.method.whereparam;

import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class BetweenXmlWhereParamNode extends CompareXmlWhereParamNode {

    public BetweenXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        super(whereParam, paramType);
    }

    @Override
    public String getCompareSymbol() {
        return "between ";
    }

    @Override
    protected String getCompareValueName() {

        return String.format("#{${begin}} and #{${end}}", this.wherePre + whereParam.getAttribute().get("beginParamName"), this.wherePre + whereParam.getAttribute().get("end"));
    }
}
