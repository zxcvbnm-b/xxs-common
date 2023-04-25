package xxs.common.module.codegenerate;

import lombok.Data;
import xxs.common.module.codegenerate.config.*;
import xxs.common.module.codegenerate.filter.*;
import xxs.common.module.codegenerate.template.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类级别代码生成的基本上下文（参数，默认配置）---基本配置 ，配置应该是在运行前完成属性修改的。可以整合springboot的自动配置
 *
 * @author xxs
 */
@Data
public class ClassCodeGenerateContext extends CodeGenerateContext {

    public ClassCodeGenerateContext() {
        initClassCodeGenerateContext();
    }

    public ClassCodeGenerateContext initClassCodeGenerateContext() {
        this.initTemplate();
        this.initGenerateFilter();
        return this;
    }

    /**
     * 初始化拦截器
     */
    private void initGenerateFilter() {
        generateFilterContext.addGenerateFilter(new ParamDTOGenerateFilter());
        generateFilterContext.addGenerateFilter(new ResultDTOGenerateFilter());
        generateFilterContext.addGenerateFilter(new StoredProcedureGenerateFilter());
    }

    /**
     * 初始化默认模板
     */
    private void initTemplate() {
        templates.add(new ServiceTemplate(new ServiceInterfaceTemplateConfig()));
        templates.add(new ControllerTemplate(new ControllerTemplateConfig()));
        templates.add(new ServiceImplTemplate(new ServiceImplTemplateConfig()));
        templates.add(new MapperTemplate(new MapperInterfaceTemplateConfig()));
        templates.add(new MapperXmlTemplate(new MapperXmlTemplateConfig()));
        templates.add(new EntityTemplate(new EntityTemplateConfig()));
    }
}
