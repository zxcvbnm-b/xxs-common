package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;

/**
 * mapper xml模板配置
 *
 * @author xxs
 */
@Setter
@Getter
public class MapperXmlTemplateConfig extends AbstractTemplateConfig { /**
     * 是否放到resources文件下，如果是，那么路径为/resources/packageSimpleName/tableName/xxxMapper.xml
     */
    private boolean resources = true;
    /**
     * 当放在 resources下时的包名
     */
    private String resourcesPackageSimpleName = "mapper";

    public MapperXmlTemplateConfig() {
        super(Constants.DEFAULT_MAPPER_PACKAGE_SIMPLE_NAME, Constants.DEFAULT_MAPPER_FILE_POST, Constants.VELOCITY_PARAM_MAPPER_XML_CONFIG_NAME);
    }
}
