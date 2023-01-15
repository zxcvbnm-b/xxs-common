package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

/*控制器模板*/
public class ControllerTemplate extends AbstractTemplate {

    public ControllerTemplate(CodeGenerateContext codeGenerateContext) {
        super(codeGenerateContext);
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/controller.java.vm";
    }

    @Override
    public String getOutFilePathName(TableInfo tableInfo) {
        return getFileName(tableInfo.getCapitalizeTableName() + codeGenerateContext.getControllerConfig()
                .getFilePost()+super.JAVA_FILE_POST, codeGenerateContext.getControllerConfig().getPackageSimpleName());
    }
}
