package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.MapperXmlTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

/*Mapper.xml模板*/
public class MapperXmlTemplate extends AbstractTemplate {

    public MapperXmlTemplate(CodeGenerateContext codeGenerateContext) {
        super(codeGenerateContext);
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/mapper.xml.vm";
    }

    @Override
    public String getOutFilePathName(TableInfo tableInfo) {
        MapperXmlTemplateConfig mapperXmlConfig = codeGenerateContext.getMapperXmlConfig();
        //如果生成到resources文件夹
        if (mapperXmlConfig.isResources()) {
            return getResourceFileName(tableInfo.getCapitalizeTableName() + mapperXmlConfig.getFilePost() + XML_FILE_POST, codeGenerateContext.getMapperXmlConfig()
            .getResourcesPackageSimpleName(), codeGenerateContext.getModuleName(), tableInfo.getTableName());
        }
        return getResourceFileName(tableInfo.getCapitalizeTableName() + mapperXmlConfig.getFilePost() + XML_FILE_POST, codeGenerateContext.getMapperXmlConfig().getPackageSimpleName());
    }

}
