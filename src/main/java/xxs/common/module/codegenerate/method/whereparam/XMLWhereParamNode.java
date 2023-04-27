package xxs.common.module.codegenerate.method.whereparam;


/**
 * xml 一个where节点  比如是一个foreach啊 还是一个等值得啊 还是一个时间范围查询啊,不同类型可能有不同的输出 比如时间范围查询，那么就需要时间参数的名称
 * TODO foreach 时间范围， in  between
 *
 * @author xxs
 */
public interface XMLWhereParamNode {
    /**
     * 比如
     *
     * @return
     */
    String getWhereParamNode();
}
