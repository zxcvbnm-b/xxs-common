package xxs.common.module.codegenerate.method.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.AbstractTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.AbstractTemplate;
import java.util.Map;

/**
 * methodAll类模板
 *
 * @author xxs
 */
public class MethodAllTemplate extends AbstractTemplate {
    private final static String TEMPLATE_NAME = "templates/method/methodAll.java.vm";

    public MethodAllTemplate(AbstractTemplateConfig abstractTemplateConfig) {
        super(abstractTemplateConfig);
    }

    @Override
    public String getTemplateFilePathName() {
        return TEMPLATE_NAME;
    }

    @Override
    public String getFileSuffix() {
        return JAVA_FILE_POST;
    }

    private String getFileSuffix(String name) {
        return JAVA_FILE_POST;
    }

    @Override
    public Map<String, Object> customTemplateParamMap(Object param) {
        return super.customTemplateParamMap(param);
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName()+"MethodAll");
    }

    @Override
    public boolean append() {
        return false;
    }
}
