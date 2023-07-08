package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.utils.other.XxsProperties;


/**
 * Service接口xml模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class ServiceInterfaceTemplateConfig extends AbstractTemplateConfig {
    public ServiceInterfaceTemplateConfig(XxsProperties properties) {
        super(properties.getString(Constants.SERVICE_INTERFACE_PACKAGE_SIMPLE_NAME_PROPERTY_NAME, Constants.DEFAULT_SERVICE_INTERFACE_PACKAGE_SIMPLE_NAME),
                properties.getString(Constants.SERVICE_INTERFACE_FILE_POST_PROPERTY_NAME, Constants.DEFAULT_SERVICE_INTERFACE_FILE_POST),
                Constants.VELOCITY_PARAM_SERVICE_INTERFACE_CONFIG_NAME);
    }
}
