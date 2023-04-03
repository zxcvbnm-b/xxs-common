package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * 控制器模板
 *
 * @author xxs
 */
public class ControllerTemplate extends AbstractTemplate {

    public ControllerTemplate() {
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/controller.java.vm";
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() + codeGenerateContext.getControllerConfig()
                .getFilePost() + JAVA_FILE_POST, codeGenerateContext.getControllerConfig().getPackageSimpleName());
    }
}
