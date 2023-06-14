package xxs.common.module.codegenerate.method.whereparam;

import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * 用于创建XMLWhereParamNode
 *
 * @author xxs
 */
public class XmlWhereParamNodeFactory {
    public static XMLWhereParamNode create(WhereParam whereParam, ParamType paramType) {
        XMLWhereParamNode xmlWhereParamNode = null;
        switch (whereParam.getWhereParamOperationType()) {
            case BETWEEN:
                xmlWhereParamNode = new BetweenXmlWhereParamNode(whereParam, paramType);
                break;
            case EQ:
                xmlWhereParamNode = new EqXmlWhereParamNode(whereParam, paramType);
                break;
            case IN:
                xmlWhereParamNode = new ForeachXmlWhereParamNode(whereParam, paramType);
                break;
            case GE:
                xmlWhereParamNode = new GEXmlWhereParamNode(whereParam, paramType);
                break;
            case GT:
                xmlWhereParamNode = new GtXmlWhereParamNode(whereParam, paramType);
                break;
            case LE:
                xmlWhereParamNode = new LEXmlWhereParamNode(whereParam, paramType);
                break;
            case LIKE:
                xmlWhereParamNode = new LikeXmlWhereParamNode(whereParam, paramType);
                break;
            case LT:
                xmlWhereParamNode = new LtXmlWhereParamNode(whereParam, paramType);
                break;
            default:
                throw new IllegalArgumentException("XMLWhereParamNode不支持" + whereParam.getWhereParamOperationType().name());
        }

        return xmlWhereParamNode;
    }
}
