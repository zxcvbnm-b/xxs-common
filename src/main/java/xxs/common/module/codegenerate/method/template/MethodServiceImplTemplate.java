package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.config.ServiceImplTemplateConfig;
import xxs.common.module.codegenerate.template.ServiceImplTemplate;

import java.util.Map;

/**
 * 服务实现类模板
 *
 * @author issuser
 */
public class MethodServiceImplTemplate extends ServiceImplTemplate {
    private final static String TEMPLATE_NAME = "templates/method/serviceImpl.java.vm";

    public MethodServiceImplTemplate(ServiceImplTemplateConfig serviceImplTemplateConfig) {
        super(serviceImplTemplateConfig);
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
