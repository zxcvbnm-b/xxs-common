package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.AbstractTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

import java.util.Map;

/*paramDTO类模板*/
public class ResultDTOTemplate extends AbstractTemplate {
    private final static String FILE_POST="Result";
    private final static String PACKAGE_SIMPLE_NAME="model.result";
    public ResultDTOTemplate(CodeGenerateContext codeGenerateContext) {
        super(codeGenerateContext);
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/resultDTO.java.vm";
    }

    @Override
    public String getOutFilePathName(TableInfo tableInfo) {
        return getFileName(tableInfo.getCapitalizeTableName() + FILE_POST + super.JAVA_FILE_POST, PACKAGE_SIMPLE_NAME);
    }
    @Override
    public Map<String, Object> getObjectValueMap() {
        Map<String, Object> objectValueMap = super.getObjectValueMap();
        objectValueMap.put("resultDTOConfig", new AbstractTemplateConfig(PACKAGE_SIMPLE_NAME,FILE_POST,codeGenerateContext) {
        });
        return objectValueMap;
    }
}
