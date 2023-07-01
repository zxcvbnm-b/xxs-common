package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;

/**
 * 实体类模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class EntityTemplateConfig extends AbstractTemplateConfig {
    public EntityTemplateConfig() {
        super(Constants.DEFAULT_ENTITY_PACKAGE_SIMPLE_NAME, Constants.DEFAULT_ENTITY_FILE_POST, Constants.VELOCITY_PARAM_ENTITY_CONFIG_NAME);
    }
}
