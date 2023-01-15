package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

/*服务实现类模板*/
public class ServiceImplTemplate extends AbstractTemplate {

    public ServiceImplTemplate(CodeGenerateContext codeGenerateContext) {
        super(codeGenerateContext);
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/serviceImpl.java.vm";
    }

    @Override
    public String getOutFilePathName(TableInfo tableInfo) {
        return getFileName(tableInfo.getCapitalizeTableName() + codeGenerateContext.getServiceImplConfig()
                .getFilePost()+super.JAVA_FILE_POST, codeGenerateContext.getServiceImplConfig().getPackageSimpleName());

    }

}
