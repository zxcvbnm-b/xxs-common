package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.Constants;

@Data
public class MapperInterfaceTemplateConfig extends AbstractTemplateConfig {
    public MapperInterfaceTemplateConfig(CodeGenerateContext codeGenerateContext) {
        super(Constants.DEFAULT_MAPPER_INTERFACE_PACKAGE_SIMPLE_NAME,Constants.DEFAULT_MAPPER_INTERFACE_FILE_POST, codeGenerateContext);
    }
}
