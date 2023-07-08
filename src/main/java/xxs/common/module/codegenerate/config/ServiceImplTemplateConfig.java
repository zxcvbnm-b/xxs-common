package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.utils.other.XxsProperties;

/**
 * ServiceImpl xml模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class ServiceImplTemplateConfig extends AbstractTemplateConfig {
    public ServiceImplTemplateConfig(XxsProperties properties) {
        super(properties.getString(Constants.SERVICE_IMPL_PACKAGE_SIMPLE_NAME_PROPERTY_NAME, Constants.DEFAULT_SERVICE_IMPL_PACKAGE_SIMPLE_NAME),
                properties.getString(Constants.SERVICE_IMPL_FILE_POST_PROPERTY_NAME, Constants.DEFAULT_SERVICE_IMPL_FILE_POST),
                Constants.VELOCITY_PARAM_SERVICE_IMPL_CONFIG_NAME);
    }
}
