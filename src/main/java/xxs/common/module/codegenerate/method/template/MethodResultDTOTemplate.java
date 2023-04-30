package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.template.ResultDTOTemplate;

import java.util.Map;

/**
 * paramDTO类模板
 *
 * @author xxs
 */
public class MethodResultDTOTemplate extends ResultDTOTemplate {
    private final static String TEMPLATE_NAME = "templates/method/resultDTO.java.vm";

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
        return false;
    }
}
