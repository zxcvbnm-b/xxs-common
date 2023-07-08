package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.utils.other.XxsProperties;

/**
 * 实体类模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class EntityTemplateConfig extends AbstractTemplateConfig {
    public EntityTemplateConfig(XxsProperties properties) {
        super(properties.getString(Constants.ENTITY_PACKAGE_SIMPLE_NAME_PROPERTY_NAME, Constants.DEFAULT_ENTITY_PACKAGE_SIMPLE_NAME),
                properties.getString(Constants.ENTITY_FILE_POST_PROPERTY_NAME, Constants.DEFAULT_ENTITY_FILE_POST),
                Constants.VELOCITY_PARAM_ENTITY_CONFIG_NAME);
    }
}
