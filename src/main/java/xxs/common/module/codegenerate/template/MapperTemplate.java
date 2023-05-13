package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.MapperInterfaceTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * mapper接口模板
 *
 * @author xxs
 */
public class MapperTemplate extends AbstractTemplate {
    private MapperInterfaceTemplateConfig mapperInterfaceTemplateConfig;
    private final static String TEMPLATE_NAME = "templates/mapper.java.vm";

    public MapperTemplate(MapperInterfaceTemplateConfig mapperInterfaceTemplateConfig) {
        super(mapperInterfaceTemplateConfig);
        this.mapperInterfaceTemplateConfig = mapperInterfaceTemplateConfig;
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
