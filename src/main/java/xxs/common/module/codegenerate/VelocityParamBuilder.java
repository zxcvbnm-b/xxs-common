package xxs.common.module.codegenerate;

import cn.hutool.core.bean.BeanUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板引擎参数构建器--用于自定义模板引擎参数
 * @author xxs
 */
public class VelocityParamBuilder {
    private Map<String, Object> velocityParam = new HashMap<>();
    private CodeGenerateContext codeGenerateContext;

    public VelocityParamBuilder(CodeGenerateContext codeGenerateContext) {
        this.codeGenerateContext = codeGenerateContext;
        init();
    }

    private VelocityParamBuilder init() {
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
