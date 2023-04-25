package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.ControllerTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

import java.util.Map;

/**
 * 控制器模板
 *
 * @author xxs
 */
public class ControllerTemplate extends AbstractTemplate {
    private ControllerTemplateConfig controllerTemplateConfig;
    private final static String TEMPLATE_NAME = "templates/controller.java.vm";

    public ControllerTemplate(ControllerTemplateConfig controllerTemplateConfig) {
        super(controllerTemplateConfig);
        this.controllerTemplateConfig = controllerTemplateConfig;
    }

    @Override
    public String getTemplateFilePathName() {
        return TEMPLATE_NAME;
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName());
    }

    @Override
    public String getFileSuffix() {
        return JAVA_FILE_POST;
    }
}
