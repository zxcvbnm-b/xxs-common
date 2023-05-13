package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.config.ServiceInterfaceTemplateConfig;
import xxs.common.module.codegenerate.template.ServiceTemplate;

import java.util.Map;


/**
 * 服务接口模板
 *
 * @author xxs
 */
public class MethodServiceTemplate extends ServiceTemplate {
    private final static String TEMPLATE_NAME = "templates/method/service.java.vm";

    public MethodServiceTemplate(ServiceInterfaceTemplateConfig serviceInterfaceTemplateConfig) {
        super(serviceInterfaceTemplateConfig);
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
