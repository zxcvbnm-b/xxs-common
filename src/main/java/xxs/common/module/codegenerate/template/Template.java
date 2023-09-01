package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

import java.util.Map;

/**
 * 模板接口
 *
 * @author xxs
 */
public interface Template {
    /**
     * 模板文件地址
     */
    String getTemplateFilePathName();

    /**
     * 输出文件地址
     */
    String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo);

    /**
     * 模板需要的额外参数
     * @param param 用户入参 用于基于入参定制模板特定参数
     * @return
     */
    Map<String, Object> customTemplateParamMap(Object param);

    /**
     * 是否追加到bean
     *
     * @return
     */
    default boolean append() {
        return false;
    }

}
