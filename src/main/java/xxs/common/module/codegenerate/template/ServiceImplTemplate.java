package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.codegenerate.config.ServiceImplTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;

/**
 * 服务实现类模板
 *
 * @author issuser
 */
public class ServiceImplTemplate extends AbstractTemplate {
    private final static String TEMPLATE_NAME = "templates/serviceImpl.java.vm";
    private ServiceImplTemplateConfig serviceImplTemplateConfig;

    public ServiceImplTemplate(ServiceImplTemplateConfig serviceImplTemplateConfig) {
        super(serviceImplTemplateConfig);
        this.serviceImplTemplateConfig = serviceImplTemplateConfig;
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
        return Constants.JAVA_FILE_POST;
    }
}
