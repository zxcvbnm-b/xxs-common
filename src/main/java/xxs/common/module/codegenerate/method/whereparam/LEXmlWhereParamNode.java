package xxs.common.module.codegenerate.method.whereparam;

import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class LEXmlWhereParamNode extends CompareXmlWhereParamNode {

    public LEXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        super(whereParam, paramType);
    }

    @Override
    public String getCompareSymbol() {
        return "<=";
    }
}
