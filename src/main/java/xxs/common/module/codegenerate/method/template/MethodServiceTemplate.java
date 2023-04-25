package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.ServiceInterfaceTemplateConfig;
import xxs.common.module.codegenerate.method.config.MethodServiceInterfaceTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.AbstractTemplate;
import xxs.common.module.codegenerate.template.ServiceTemplate;


/**
 * 服务接口模板
 *
 * @author xxs
 */
public class MethodServiceTemplate extends ServiceTemplate {
    private final static String TEMPLATE_NAME = "templates/method/service.java.vm";

    public MethodServiceTemplate(MethodServiceInterfaceTemplateConfig serviceInterfaceTemplateConfig) {
        super(serviceInterfaceTemplateConfig);
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
