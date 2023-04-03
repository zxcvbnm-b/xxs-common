package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.AbstractTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

import java.util.Map;

/**
 * paramDTO类模板
 *
 * @author xxs
 */
public class ParamDTOTemplate extends AbstractTemplate {
    private final static String FILE_POST = "Param";
    private final static String PACKAGE_SIMPLE_NAME = "model.param";

    public ParamDTOTemplate() {
        super();
    }

    @Override
    public String getTemplateFilePathName() {
        return "templates/paramDTO.java.vm";
    }

    @Override
    public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
        return getFileName(codeGenerateContext, tableInfo.getCapitalizeTableName() + FILE_POST + JAVA_FILE_POST, PACKAGE_SIMPLE_NAME);
    }

    @Override
    public Map<String, Object> getObjectValueMap() {
        Map<String, Object> objectValueMap = super.getObjectValueMap();
        objectValueMap.put("paramDTOConfig", new AbstractTemplateConfig(PACKAGE_SIMPLE_NAME, FILE_POST) {
        });
        return objectValueMap;
    }
}
