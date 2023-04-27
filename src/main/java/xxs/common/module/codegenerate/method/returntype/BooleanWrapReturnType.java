package xxs.common.module.codegenerate.method.returntype;

/**
 * boolean
 * @author xxs
 */
public class BooleanWrapReturnType implements ReturnType{
    @Override
    public String methodReturnTypeString() {
        return "boolean";
    }

    @Override
    public String methodXmlReturnTypeString() {
        return boolean.class.getName();
    }
}
