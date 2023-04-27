package xxs.common.module.codegenerate.method.returntype;

/**
 * Long
 * @author xxs
 */
public class LongWrapReturnType implements ReturnType{
    @Override
    public String methodReturnTypeString() {
        return "Long";
    }

    @Override
    public String methodXmlReturnTypeString() {
        return Long.class.getName();
    }
}
