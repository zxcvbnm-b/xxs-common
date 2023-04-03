package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * mapper接口模板
 *
 * @author xxs
 */
public class MapperTemplate extends AbstractTemplate {

    public MapperTemplate() {
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/mapper.java.vm";
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() + codeGenerateContext.getMapperInterfaceConfig()
                .getFilePost() + JAVA_FILE_POST, codeGenerateContext.getMapperInterfaceConfig().getPackageSimpleName());
    }

}
