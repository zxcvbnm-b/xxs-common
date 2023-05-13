package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.EntityTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * 实体类模板
 *
 * @author xxs
 */
public class EntityTemplate extends AbstractTemplate {
    private EntityTemplateConfig entityTemplateConfig;
    private final static String TEMPLATE_NAME = "templates/entity.java.vm";

    public EntityTemplate(EntityTemplateConfig entityTemplateConfig) {
        super(entityTemplateConfig);
        this.entityTemplateConfig = entityTemplateConfig;
    }

    @Override
    public String getTemplateFilePathName() {
        return TEMPLATE_NAME;
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName());
    }

    @Override
    public String getFileSuffix() {
        return JAVA_FILE_POST;
    }
}
