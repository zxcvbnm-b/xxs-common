package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.MapperXmlTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * Mapper.xml模板
 *
 * @author xxs
 */
public class MapperXmlTemplate extends AbstractTemplate {
    private MapperXmlTemplateConfig mapperXmlTemplateConfig;
    private final static String TEMPLATE_NAME = "templates/mapper.xml.vm";

    public MapperXmlTemplate(MapperXmlTemplateConfig mapperXmlTemplateConfig) {
        super(mapperXmlTemplateConfig);
        this.mapperXmlTemplateConfig = mapperXmlTemplateConfig;
    }

    @Override
    public String getTemplateFilePathName() {
        return TEMPLATE_NAME;
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        //如果生成到resources文件夹
        if (mapperXmlTemplateConfig.isResources()) {
            return getResourceFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() );
        }
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName());
    }
    @Override
    public String getFileSuffix() {
        return XML_FILE_POST;
    }
}
