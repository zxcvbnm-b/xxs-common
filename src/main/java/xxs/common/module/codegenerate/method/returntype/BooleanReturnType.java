package xxs.common.module.codegenerate.method.returntype;

/**
 * Boolean
 * @author xxs
 */
public class BooleanReturnType implements ReturnType{
    @Override
    public String methodReturnTypeString() {
        return "Boolean";
    }

    @Override
    public String methodXmlReturnTypeString() {
        return Boolean.class.getName();
    }
}
