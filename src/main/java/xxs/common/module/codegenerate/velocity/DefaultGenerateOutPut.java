package xxs.common.module.codegenerate.velocity;

/**
 * 输出到工作台
 *
 * @author xiongsongxu
 */
public class DefaultGenerateOutPut implements GenerateOutPut {
    @Override
    public void output(String name, String stringWriterText) {
        System.out.println("=================================" + name + "==============================");
        System.out.println(stringWriterText);
    }
}
