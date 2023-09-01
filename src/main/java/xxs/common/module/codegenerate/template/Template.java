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
     * 获取模板文件地址（或者模板名称）
     */
    String getTemplateFilePathName();

    /**
     * 获取输出文件地址
     */
    String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo);

    /**
     * 模板需要的额外参数
     * @param param 用户入参 用于基于入参定制模板特定参数
     * @return 在模板文件中使用的参数
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

    /**
     * 获取文件名称
     * @return
     */
    String getFileName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo);

}
