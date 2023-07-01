package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;
/**
 * ServiceImpl xml模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class ServiceImplTemplateConfig extends AbstractTemplateConfig {
    public ServiceImplTemplateConfig() {
        super(Constants.DEFAULT_SERVICE_IMPL_PACKAGE_SIMPLE_NAME,Constants.DEFAULT_SERVICE_IMPL_FILE_POST,Constants.VELOCITY_PARAM_SERVICE_IMPL_CONFIG_NAME);
    }
}
