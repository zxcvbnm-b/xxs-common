package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * 实体类模板
 *
 * @author xxs
 */
public class EntityTemplate extends AbstractTemplate {

    public EntityTemplate(CodeGenerateContext codeGenerateContext) {
        super(codeGenerateContext);
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/entity.java.vm";
    }

    @Override
    public String getOutFilePathName(TableInfo tableInfo) {
        return getFileName(tableInfo.getCapitalizeTableName() + codeGenerateContext.getEntityConfig().getFilePost() + super.JAVA_FILE_POST, codeGenerateContext.getEntityConfig().getPackageSimpleName());
    }

}
