package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.ControllerTemplateConfig;
import xxs.common.module.codegenerate.method.config.MethodControllerTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.AbstractTemplate;
import xxs.common.module.codegenerate.template.ControllerTemplate;

import java.util.Map;

/**
 * 控制器模板
 *
 * @author xxs
 */
public class MethodControllerTemplate extends ControllerTemplate {
    private final static String TEMPLATE_NAME = "templates/method/mapper.java.vm";

    public MethodControllerTemplate(MethodControllerTemplateConfig controllerTemplateConfig) {
        super(controllerTemplateConfig);
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
