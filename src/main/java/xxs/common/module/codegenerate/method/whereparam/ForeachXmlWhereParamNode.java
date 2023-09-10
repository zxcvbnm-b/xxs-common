package xxs.common.module.codegenerate.method.whereparam;

import com.alibaba.druid.DbType;
import xxs.common.module.codegenerate.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class ForeachXmlWhereParamNode extends CompareXmlWhereParamNode {
    static final String FOREACH_TEMPLATE
            = "\n"+"        <foreach item=\"item\" index=\"index\" collection=\"${paramName}\" open=\"(\" separator=\",\" close=\")\">\n" +
            "            #{item}\n" +
            "        </foreach>";

    public ForeachXmlWhereParamNode(DbType dbType, WhereParam whereParam, ParamType paramType) {
        super(dbType, whereParam, paramType);
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
