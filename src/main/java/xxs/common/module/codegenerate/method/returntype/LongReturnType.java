package xxs.common.module.codegenerate.method.returntype;

/**
 * Long
 * @author xxs
 */
public class LongReturnType implements ReturnType{
    @Override
    public String methodReturnTypeString() {
        return "long";
    }

    @Override
    public String methodXmlReturnTypeString() {
        return long.class.getName();
    }
}
