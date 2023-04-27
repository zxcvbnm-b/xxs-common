package xxs.common.module.codegenerate.method.whereparam;

import cn.hutool.core.util.StrUtil;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.enums.WhereSearchParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;
import xxs.common.module.codegenerate.model.SearchColumnInfo;

import java.util.Properties;

/**
 * @author xxs
 */
public class EqXmlWhereParamNode extends CompareXmlWhereParamNode {

    public EqXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        super(whereParam, paramType);
    }

    @Override
    public String getCompareSymbol() {
        return "=";
    }
}
