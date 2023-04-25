package xxs.common.module.codegenerate;

import cn.hutool.core.bean.BeanUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板引擎参数构建器--用于自定义模板引擎参数
 *
 * @author xxs
 */
public class VelocityParamBuilder {
    private Map<String, Object> velocityParam = new HashMap<>();
    private CodeGenerateContext codeGenerateContext;

    public VelocityParamBuilder(CodeGenerateContext codeGenerateContext) {
        this.codeGenerateContext = codeGenerateContext;
    }

    public void put(String key, Object value) {
        velocityParam.put(key, value);
        return;
    }

    public void putAll(Map<String, Object> stringObjectMap) {
        velocityParam.putAll(stringObjectMap);
        return;
    }

    public Map<String, Object> get() {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(codeGenerateContext);
        Map<String, Object> otherGlobalConfigMap = codeGenerateContext.getOtherGlobalConfigMap();
        velocityParam.putAll(otherGlobalConfigMap);
        velocityParam.putAll(stringObjectMap);
        return velocityParam;
    }
}
