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

    public MapperXmlTemplate() {
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/mapper.xml.vm";
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        MapperXmlTemplateConfig mapperXmlConfig = codeGenerateContext.getMapperXmlConfig();
        //如果生成到resources文件夹
        if (mapperXmlConfig.isResources()) {
            return getResourceFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() + mapperXmlConfig.getFilePost() + XML_FILE_POST, codeGenerateContext.getMapperXmlConfig().getResourcesPackageSimpleName());
        }
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() + mapperXmlConfig.getFilePost() + XML_FILE_POST, codeGenerateContext.getMapperXmlConfig().getPackageSimpleName());
    }

}
