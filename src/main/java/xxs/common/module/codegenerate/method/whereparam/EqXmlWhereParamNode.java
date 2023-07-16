package xxs.common.module.codegenerate.method.whereparam;

import com.alibaba.druid.DbType;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class EqXmlWhereParamNode extends CompareXmlWhereParamNode {

    public EqXmlWhereParamNode(DbType dbType, WhereParam whereParam, ParamType paramType) {
        super(dbType, whereParam, paramType);
    }

    @Override
    public String getCompareSymbol() {
        return "=";
    }
}
