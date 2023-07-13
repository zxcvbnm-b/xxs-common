package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.utils.other.XxsProperties;

/**
 * mapper xml模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class MapperXmlTemplateConfig extends AbstractTemplateConfig {
    /**
     * 是否放到resources文件下，如果是，那么路径为/resources/packageSimpleName/tableName/xxxMapper.xml
     */
    private boolean resources;
    /**
     * 当放在 resources下时的包名
     */
    private String resourcesPackageSimpleName;

    public MapperXmlTemplateConfig(XxsProperties properties) {
        super(properties.getString(Constants.MAPPER_PACKAGE_SIMPLE_NAME_PROPERTY_NAME, Constants.DEFAULT_MAPPER_PACKAGE_SIMPLE_NAME),
                properties.getString(Constants.MAPPER_FILE_POST_PROPERTY_NAME, Constants.DEFAULT_MAPPER_FILE_POST),
                Constants.VELOCITY_PARAM_MAPPER_XML_CONFIG_NAME);
        this.resources = properties.getBoolean(Constants.MAPPER_RESOURCES_PROPERTY_NAME, true);
        this.resourcesPackageSimpleName = properties.getString(Constants.MAPPER_RESOURCES_PACKAGE_SIMPLE_NAME_PROPERTY_NAME, Constants.DEFAULT_MAPPER_RESOURCES_PACKAGE_SIMPLE_NAME);

    }
}
