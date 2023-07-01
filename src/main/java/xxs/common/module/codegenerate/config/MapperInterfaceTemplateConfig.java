package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;

/**
 * mapper接口模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class MapperInterfaceTemplateConfig extends AbstractTemplateConfig {
    public MapperInterfaceTemplateConfig() {
        super(Constants.DEFAULT_MAPPER_INTERFACE_PACKAGE_SIMPLE_NAME, Constants.DEFAULT_MAPPER_INTERFACE_FILE_POST, Constants.VELOCITY_PARAM_MAPPER_INTERFACE_CONFIG_NAME);
    }
}
