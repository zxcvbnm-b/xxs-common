package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.Constants;

/**
 * mapper接口模板配置
 *
 * @author xxs
 */
@Data
public class MapperInterfaceTemplateConfig extends AbstractTemplateConfig {
    public MapperInterfaceTemplateConfig() {
        super(Constants.DEFAULT_MAPPER_INTERFACE_PACKAGE_SIMPLE_NAME, Constants.DEFAULT_MAPPER_INTERFACE_FILE_POST, "mapperInterfaceConfig");
    }
}
