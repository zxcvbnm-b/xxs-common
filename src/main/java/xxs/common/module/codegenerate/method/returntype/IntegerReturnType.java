package xxs.common.module.codegenerate.method.returntype;

/**
 * Integer
 * @author xxs
 */
public class IntegerReturnType implements ReturnType{
    @Override
    public String methodReturnTypeString() {
        return "Integer";
    }

    @Override
    public String methodXmlReturnTypeString() {
        return Integer.class.getName();
    }
}
