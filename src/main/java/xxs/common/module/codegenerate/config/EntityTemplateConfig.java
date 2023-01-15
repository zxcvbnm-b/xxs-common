package xxs.common.module.codegenerate.config;

import lombok.Data;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.Constants;

@Data
public class EntityTemplateConfig extends AbstractTemplateConfig {
    public EntityTemplateConfig(CodeGenerateContext codeGenerateContext) {
        super(Constants.DEFAULT_ENTITY_PACKAGE_SIMPLE_NAME,Constants.DEFAULT_ENTITY_FILE_POST, codeGenerateContext);
    }
}
