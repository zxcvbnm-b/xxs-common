package xxs.common.module.codegenerate.velocity;

/**
 * 模板生成输出
 *
 * @author xiongsongxu
 */
public interface GenerateOutPut {
    /**
     * 模板引擎数据渲染后输出
     *
     * @param stringWriterText velocity模板引擎渲染之后得模板内容
     * @param name 标识名字
     */
    void output(String name ,String stringWriterText) throws Exception;
}
