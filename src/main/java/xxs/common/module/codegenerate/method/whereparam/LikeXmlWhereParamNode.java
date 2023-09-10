package xxs.common.module.codegenerate.method.whereparam;

import com.alibaba.druid.DbType;
import xxs.common.module.codegenerate.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
public class LikeXmlWhereParamNode extends CompareXmlWhereParamNode {

    public LikeXmlWhereParamNode(DbType dbType, WhereParam whereParam, ParamType paramType) {
        super(dbType, whereParam, paramType);
    }

    @Override
    public String getCompareSymbol() {
        if(dbType.equals(DbType.postgresql)){
            return "ILIKE ";
        }
        return "LIKE ";
    }

    @Override
    protected String getCompareValueName() {
        String result;
        switch (dbType) {
            case mysql:
                result = "CONCAT('%'," + DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE + ", '%')";
                break;
            default:
                result = "'%" + DEFAULT_COMPARE_PARAM_TEMPLATE_VALUE + "%'";
                break;
        }
        return result;
    }

}
