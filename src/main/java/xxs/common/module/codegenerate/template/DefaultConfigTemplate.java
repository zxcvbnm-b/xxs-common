package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.AbstractTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

import java.util.Map;

/**
 * 默认模板
 *
 * @author xxs
 */
public class DefaultConfigTemplate extends AbstractTemplate {
    private String templateFilePathName;
    private String outFilePathName;

    public DefaultConfigTemplate(CodeGenerateContext codeGenerateContext, String templateFilePathName) {
        super(codeGenerateContext);
        this.templateFilePathName = templateFilePathName;
    }

    @Override
    public String getTemplateFilePathName() {
        return templateFilePathName;
    }

    @Override
    public String getOutFilePathName(TableInfo tableInfo) {
        return outFilePathName;
    }
}
