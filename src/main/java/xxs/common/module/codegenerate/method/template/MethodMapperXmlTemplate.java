package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.config.MapperXmlTemplateConfig;
import xxs.common.module.codegenerate.template.MapperXmlTemplate;

import java.util.Map;

/**
 * Mapper.xml模板
 *
 * @author xxs
 */
public class MethodMapperXmlTemplate extends MapperXmlTemplate {
    private final static String TEMPLATE_NAME = "templates/method/mapper.xml.vm";

    public MethodMapperXmlTemplate(MapperXmlTemplateConfig mapperXmlTemplateConfig) {
        super(mapperXmlTemplateConfig);
    }

    @Override
    public String getTemplateFilePathName() {
        return TEMPLATE_NAME;
    }

    @Override
    public Map<String, Object> customTemplateParamMap(Object param) {
        return super.customTemplateParamMap(param);
    }

    @Override
    public boolean append() {
        return true;
    }
}
