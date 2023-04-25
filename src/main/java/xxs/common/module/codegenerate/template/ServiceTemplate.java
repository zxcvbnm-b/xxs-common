package xxs.common.module.codegenerate.template;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.ServiceInterfaceTemplateConfig;
import xxs.common.module.codegenerate.model.TableInfo;


/**
 * 服务接口模板
 *
 * @author xxs
 */
public class ServiceTemplate extends AbstractTemplate {
    ServiceInterfaceTemplateConfig serviceInterfaceConfig;
    private final static String TEMPLATE_NAME = "templates/service.java.vm";

    public ServiceTemplate(ServiceInterfaceTemplateConfig serviceInterfaceTemplateConfig) {
        super(serviceInterfaceTemplateConfig);
        this.serviceInterfaceConfig = serviceInterfaceTemplateConfig;
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
