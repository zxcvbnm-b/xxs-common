package xxs.common.module.codegenerate.method.whereparam;

import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class LikeXmlWhereParamNode extends CompareXmlWhereParamNode {

    public LikeXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        super(whereParam, paramType);
    }

    @Override
    public String getCompareSymbol() {
        return "like ";
    }

    @Override
    protected String getCompareValueName() {

        return "concat('%'," + DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE + ", '%')";
    }
}
