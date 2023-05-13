package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.Constants;

/**
 * 实体类模板配置
 *
 * @author xxs
 */
@Data
public class EntityTemplateConfig extends AbstractTemplateConfig {
    public EntityTemplateConfig() {
        super(Constants.DEFAULT_ENTITY_PACKAGE_SIMPLE_NAME, Constants.DEFAULT_ENTITY_FILE_POST, "entityConfig");
    }
}
