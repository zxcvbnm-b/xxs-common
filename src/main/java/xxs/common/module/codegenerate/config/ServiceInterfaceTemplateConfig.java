package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;

/**
 * Service接口xml模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class ServiceInterfaceTemplateConfig extends AbstractTemplateConfig {
    public ServiceInterfaceTemplateConfig() {
        super(Constants.DEFAULT_SERVICE_INTERFACE_PACKAGE_SIMPLE_NAME, Constants.DEFAULT_SERVICE_INTERFACE_FILE_POST, Constants.VELOCITY_PARAM_SERVICE_INTERFACE_CONFIG_NAME);
    }
}
