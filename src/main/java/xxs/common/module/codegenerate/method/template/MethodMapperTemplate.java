package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.MapperInterfaceTemplateConfig;
import xxs.common.module.codegenerate.method.config.MethodMapperInterfaceTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.AbstractTemplate;
import xxs.common.module.codegenerate.template.MapperTemplate;

/**
 * mapper接口模板
 *
 * @author xxs
 */
public class MethodMapperTemplate extends MapperTemplate {
    private final static String TEMPLATE_NAME = "templates/method/mapper.java.vm";

    public MethodMapperTemplate(MethodMapperInterfaceTemplateConfig mapperInterfaceTemplateConfig) {
        super(mapperInterfaceTemplateConfig);
    }

    @Override
    public String getTemplateFilePathName() {
        return TEMPLATE_NAME;
    }

    @Override
    public boolean append() {
        return true;
    }
}
