package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.CodeGenerateContext;

/**
 * 默认的模板配置
 * @author xxs
 */
@Data
public  class DefaultTemplateTemplateConfig extends AbstractTemplateConfig {
    public DefaultTemplateTemplateConfig(CodeGenerateContext codeGenerateContext, String packageSimpleName, String filePost) {
        super(filePost,packageSimpleName, codeGenerateContext);
    }
}
