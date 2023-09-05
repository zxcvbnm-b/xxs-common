package xxs.common.module.codegenerate;

import lombok.Data;
import xxs.common.module.codegenerate.template.*;

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
        return this;
    }

    /**
     * 初始化默认模板
     */
    private void initTemplate() {
        templates.add(new ServiceTemplate(serviceInterfaceTemplateConfig));
        templates.add(new ControllerTemplate(controllerTemplateConfig));
        templates.add(new ServiceImplTemplate(serviceImplTemplateConfig));
        templates.add(new MapperTemplate(mapperInterfaceTemplateConfig));
        templates.add(new MapperXmlTemplate(mapperXmlTemplateConfig));
        templates.add(new EntityTemplate(entityTemplateConfig));
    }
}
