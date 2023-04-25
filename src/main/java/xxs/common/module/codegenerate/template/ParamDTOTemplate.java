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
    private final static String CONFIG_NAME = "paramDTOConfig";
    private final static String TEMPLATE_NAME = "templates/paramDTO.java.vm";

    public ParamDTOTemplate() {
        super(new AbstractTemplateConfig(PACKAGE_SIMPLE_NAME, FILE_POST, CONFIG_NAME) {
        });
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
