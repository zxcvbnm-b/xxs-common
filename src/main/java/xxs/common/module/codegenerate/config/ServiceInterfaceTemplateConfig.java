package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.Constants;

@Data
public class ServiceInterfaceTemplateConfig extends AbstractTemplateConfig {
    public ServiceInterfaceTemplateConfig(CodeGenerateContext codeGenerateContext) {
        super(Constants.DEFAULT_SERVICE_INTERFACE_PACKAGE_SIMPLE_NAME,Constants.DEFAULT_SERVICE_INTERFACE_FILE_POST, codeGenerateContext);
    }
}
