package xxs.common.module.codegenerate.method.returntype;

/**
 * 返回值类型接口 比如定义各种返回值类型的类，然后让用户选择用哪些，
 * @author xxs
 */
public interface ReturnType {
    /**
     * 方法返回类型字符串，比如 int Integer ResultDto等
     * @return
     */
    String methodReturnTypeString();

    /**
     * xml配置文件的 resultType
     * @return
     */
    String methodXmlReturnTypeString();
}
