package xxs.common.module.codegenerate.method.whereparam;

import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class ForeachXmlWhereParamNode extends CompareXmlWhereParamNode {
    static final String FOREACH_TEMPLATE
            = "\n"+"        <foreach item=\"item\" index=\"index\" collection=\"${paramName}\" open=\"(\" separator=\",\" close=\")\">\n" +
            "            #{item}\n" +
            "        </foreach>";

    public ForeachXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        super(whereParam, paramType);
    }

    @Override
    public String getCompareSymbol() {
        return " in ";
    }

    @Override
    protected String getCompareValueName() {
        return FOREACH_TEMPLATE;
    }
}
