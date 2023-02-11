package xxs.common.module.codegenerate;

import cn.hutool.core.bean.BeanUtil;

import java.util.HashMap;
import java.util.Map;

public class VelocityParamBuilder {
    private Map<String, Object> velocityParam = new HashMap<>();
    private CodeGenerateContext codeGenerateContext;

    public VelocityParamBuilder(CodeGenerateContext codeGenerateContext) {
        this.codeGenerateContext = codeGenerateContext;
        init();
    }

    private VelocityParamBuilder init() {
//        velocityParam.put("basePackageName", codeGenerateContext.getBasePackageName());
//        velocityParam.put("mapperXmlPathName", codeGenerateContext.getMapperXmlPathName());
//        velocityParam.put("moduleName", codeGenerateContext.getModuleName());
//        velocityParam.put("author", codeGenerateContext.getAuthor());
//        velocityParam.put("generateTime", codeGenerateContext.getGenerateTime());
//        velocityParam.put("lombok", codeGenerateContext.isLombok());
//        velocityParam.put("swagger", codeGenerateContext.isSwagger());
//        velocityParam.put("jsr303Verify", codeGenerateContext.isJsr303Verify());
//        velocityParam.put("mybatisPlus", codeGenerateContext.isMybatisPlus());
//        velocityParam.put("genValidMethod", codeGenerateContext.isMybatisPlus());
//        velocityParam.put(Constants.VELOCITY_PARAM_CONTROLLER_CONFIG_NAME, codeGenerateContext.getControllerConfig());
//        velocityParam.put(Constants.VELOCITY_PARAM_ENTITY_CONFIG_NAME, codeGenerateContext.getEntityConfig());
//        velocityParam.put(Constants.VELOCITY_PARAM_MAPPER_INTERFACE_CONFIG_NAME, codeGenerateContext.getMapperInterfaceConfig());
//        velocityParam.put(Constants.VELOCITY_PARAM_MAPPER_XML_CONFIG_NAME, codeGenerateContext.getMapperXmlConfig());
//        velocityParam.put(Constants.VELOCITY_PARAM_SERVICE_IMPL_CONFIG_NAME, codeGenerateContext.getServiceImplConfig());
//        velocityParam.put(Constants.VELOCITY_PARAM_SERVICE_INTERFACE_CONFIG_NAME, codeGenerateContext.getServiceInterfaceConfig());
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(codeGenerateContext);
        velocityParam.putAll(stringObjectMap);
        return this;
    }

    public VelocityParamBuilder put(String key, Object value) {
        velocityParam.put(key, value);
        return this;
    }

    public Map<String, Object> get() {
        return velocityParam;
    }
}
