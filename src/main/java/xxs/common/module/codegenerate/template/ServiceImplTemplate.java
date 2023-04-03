package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * 服务实现类模板
 *
 * @author issuser
 */
public class ServiceImplTemplate extends AbstractTemplate {

    public ServiceImplTemplate() {
        super();
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/serviceImpl.java.vm";
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() + codeGenerateContext.getServiceImplConfig()
                .getFilePost() + JAVA_FILE_POST, codeGenerateContext.getServiceImplConfig().getPackageSimpleName());

    }

}
