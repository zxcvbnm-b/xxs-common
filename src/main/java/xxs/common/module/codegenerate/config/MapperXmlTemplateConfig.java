package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.Constants;

@Data
public class MapperXmlTemplateConfig extends AbstractTemplateConfig {
    private CodeGenerateContext codeGenerateContext;
    //是否放到resources文件下，如果是，那么路径为/packageSimpleName/tableName/xxxMapper.xml
    private  boolean resources = true;
    //当放在 resources下时的包名
    private String resourcesPackageSimpleName = "mapper";
    public MapperXmlTemplateConfig(CodeGenerateContext codeGenerateContext) {
        super(Constants.DEFAULT_MAPPER_PACKAGE_SIMPLE_NAME,Constants.DEFAULT_MAPPER_FILE_POST, codeGenerateContext);
    }
}
