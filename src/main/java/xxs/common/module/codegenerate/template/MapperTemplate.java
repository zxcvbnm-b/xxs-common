package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

/*mapper接口模板*/
public class MapperTemplate extends AbstractTemplate {

    public MapperTemplate(CodeGenerateContext codeGenerateContext) {
        super(codeGenerateContext);
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/mapper.java.vm";
    }

    @Override
    public String getOutFilePathName(TableInfo tableInfo) {
        return getFileName(tableInfo.getCapitalizeTableName() + codeGenerateContext.getMapperInterfaceConfig()
                .getFilePost()+super.JAVA_FILE_POST, codeGenerateContext.getMapperInterfaceConfig().getPackageSimpleName());
    }

}
