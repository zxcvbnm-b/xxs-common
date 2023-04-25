package xxs.common.module.codegenerate.method;

import lombok.Data;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.*;
import xxs.common.module.codegenerate.filter.ParamDTOGenerateFilter;
import xxs.common.module.codegenerate.filter.ResultDTOGenerateFilter;
import xxs.common.module.codegenerate.filter.StoredProcedureGenerateFilter;
import xxs.common.module.codegenerate.template.*;

/**
 * 方法级别代码生成的基本上下文（参数，默认配置）---基本配置 ，配置应该是在运行前完成属性修改的。可以整合springboot的自动配置
 *
 * @author xxs
 */
@Data
public class MethodCodeGenerateContext extends CodeGenerateContext {

    public MethodCodeGenerateContext() {
        initMethodCodeGenerateContext();
    }

    public MethodCodeGenerateContext initMethodCodeGenerateContext() {
        this.initTemplate();
        this.initTemplateConfig();
        return this;
    }

    /**
     * 初始化模板配置
     */
    private void initTemplateConfig() {
    }

    /**
     * 初始化默认模板
     */
    private void initTemplate() {
    }
}
