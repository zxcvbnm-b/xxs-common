package xxs.common.module.codegenerate.method.whereparam;

import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * xml 一个where节点  比如是一个foreach啊 还是一个等值得啊 还是一个时间范围查询啊,不同类型可能有不同的输出 比如时间范围查询，那么就需要时间参数的名称
 *
 * @author xxs
 */
public interface XMLWhereParamNode {
    /**
     * 比如
     *
     * @return
     */
    String getWhereParamNode(WhereParam whereParam, ParamType paramType);
}
