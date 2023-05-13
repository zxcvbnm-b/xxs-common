package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.config.ControllerTemplateConfig;
import xxs.common.module.codegenerate.template.ControllerTemplate;

import java.util.Map;

/**
 * 控制器模板
 *
 * @author xxs
 */
public class MethodControllerTemplate extends ControllerTemplate {
    private final static String TEMPLATE_NAME = "templates/method/controller.java.vm";

    public MethodControllerTemplate(ControllerTemplateConfig controllerTemplateConfig) {
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
