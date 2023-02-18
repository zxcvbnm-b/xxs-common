package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;


/**
 * 服务接口模板
 *
 * @author xxs
 */
public class ServiceTemplate extends AbstractTemplate {

    public ServiceTemplate(CodeGenerateContext codeGenerateContext) {
        super(codeGenerateContext);
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/service.java.vm";
    }

    @Override
    public String getOutFilePathName(TableInfo tableInfo) {
        return getFileName(tableInfo.getCapitalizeTableName() + codeGenerateContext.getServiceInterfaceConfig()
                .getFilePost() + super.JAVA_FILE_POST, codeGenerateContext.getServiceInterfaceConfig().getPackageSimpleName());
    }

}
