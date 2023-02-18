package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.Constants;
/**
 * ServiceImpl xml模板配置
 *
 * @author xxs
 */
@Data
public class ServiceImplTemplateConfig extends AbstractTemplateConfig {
    public ServiceImplTemplateConfig(CodeGenerateContext codeGenerateContext) {
        super(Constants.DEFAULT_SERVICE_IMPL_PACKAGE_SIMPLE_NAME,Constants.DEFAULT_SERVICE_IMPL_FILE_POST, codeGenerateContext);
    }
}
