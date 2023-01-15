package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.CodeGenerateContext;

/*像DTO这些*/
@Data
public  class DefaultTemplateTemplateConfig extends AbstractTemplateConfig {
    public DefaultTemplateTemplateConfig(CodeGenerateContext codeGenerateContext, String packageSimpleName, String filePost) {
        super(filePost,packageSimpleName, codeGenerateContext);
    }
}
