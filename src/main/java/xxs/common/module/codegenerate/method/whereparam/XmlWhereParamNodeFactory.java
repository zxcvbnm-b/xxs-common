package xxs.common.module.codegenerate.method.whereparam;

import com.alibaba.druid.DbType;
import xxs.common.module.codegenerate.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * 用于创建XMLWhereParamNode
 *
 * @author xxs
 */
public class XmlWhereParamNodeFactory {
    public static XMLWhereParamNode create(DbType dbType, WhereParam whereParam, ParamType paramType) {
        XMLWhereParamNode xmlWhereParamNode = null;
        switch (whereParam.getWhereParamOperationType()) {
            case BETWEEN:
                xmlWhereParamNode = new BetweenXmlWhereParamNode(dbType, whereParam, paramType);
                break;
            case EQ:
                xmlWhereParamNode = new EqXmlWhereParamNode(dbType, whereParam, paramType);
                break;
            case IN:
                xmlWhereParamNode = new ForeachXmlWhereParamNode(dbType, whereParam, paramType);
                break;
            case GE:
                xmlWhereParamNode = new GEXmlWhereParamNode(dbType, whereParam, paramType);
                break;
            case GT:
                xmlWhereParamNode = new GtXmlWhereParamNode(dbType, whereParam, paramType);
                break;
            case LE:
                xmlWhereParamNode = new LEXmlWhereParamNode(dbType, whereParam, paramType);
                break;
            case LIKE:
                xmlWhereParamNode = new LikeXmlWhereParamNode(dbType, whereParam, paramType);
                break;
            case LT:
                xmlWhereParamNode = new LtXmlWhereParamNode(dbType, whereParam, paramType);
                break;
            default:
                throw new IllegalArgumentException("XMLWhereParamNode不支持" + whereParam.getWhereParamOperationType().name());
        }

        return xmlWhereParamNode;
    }
}
