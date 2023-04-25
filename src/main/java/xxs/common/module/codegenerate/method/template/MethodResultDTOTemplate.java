package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.AbstractTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.AbstractTemplate;
import xxs.common.module.codegenerate.template.ResultDTOTemplate;

/**
 * paramDTO类模板
 *
 * @author xxs
 */
public class MethodResultDTOTemplate extends ResultDTOTemplate {
    private final static String TEMPLATE_NAME = "templates/method/mapper.xml.vm";

    @Override
    public String getTemplateFilePathName() {
        return TEMPLATE_NAME;
    }

    @Override
    public boolean append() {
        return false;
    }
}
