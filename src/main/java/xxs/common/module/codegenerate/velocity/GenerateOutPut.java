package xxs.common.module.codegenerate.velocity;

import java.io.IOException;

/**
 * 模板生成输出
 *
 * @author xiongsongxu
 */
public interface GenerateOutPut {
    /**
     * 模板引擎数据渲染后输出
     *
     * @param stringWriterText
     * @param name 标识名字
     */
    void output(String name ,String stringWriterText) throws Exception;
}
