package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;


/**
 * 服务接口模板
 *
 * @author xxs
 */
public class ServiceTemplate extends AbstractTemplate {

    public ServiceTemplate() {
        super();
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/service.java.vm";
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() + codeGenerateContext.getServiceInterfaceConfig()
                .getFilePost() + JAVA_FILE_POST, codeGenerateContext.getServiceInterfaceConfig().getPackageSimpleName());
    }

}
