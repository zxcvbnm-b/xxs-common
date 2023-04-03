package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * 实体类模板
 *
 * @author xxs
 */
public class EntityTemplate extends AbstractTemplate {

    public EntityTemplate() {
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/entity.java.vm";
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() + codeGenerateContext.getEntityConfig().getFilePost() + JAVA_FILE_POST, codeGenerateContext.getEntityConfig().getPackageSimpleName());
    }

}
