package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.ServiceImplTemplateConfig;
import xxs.common.module.codegenerate.method.config.MethodServiceImplTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.AbstractTemplate;
import xxs.common.module.codegenerate.template.ServiceImplTemplate;

/**
 * 服务实现类模板
 *
 * @author issuser
 */
public class MethodServiceImplTemplate extends ServiceImplTemplate {
    private final static String TEMPLATE_NAME = "templates/method/serviceImpl.java.vm";

    public MethodServiceImplTemplate(MethodServiceImplTemplateConfig serviceImplTemplateConfig) {
        super(serviceImplTemplateConfig);
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
