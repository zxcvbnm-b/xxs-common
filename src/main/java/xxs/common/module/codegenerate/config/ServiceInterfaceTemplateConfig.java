package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.Constants;

/**
 * Service接口xml模板配置
 *
 * @author xxs
 */
@Data
public class ServiceInterfaceTemplateConfig extends AbstractTemplateConfig {
    public ServiceInterfaceTemplateConfig() {
        super(Constants.DEFAULT_SERVICE_INTERFACE_PACKAGE_SIMPLE_NAME, Constants.DEFAULT_SERVICE_INTERFACE_FILE_POST, "serviceInterfaceConfig");
    }
}
