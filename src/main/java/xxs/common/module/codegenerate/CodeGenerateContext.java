package xxs.common.module.codegenerate;

import lombok.Data;
import xxs.common.module.codegenerate.config.*;
import xxs.common.module.codegenerate.filter.GenerateFilterContext;
import xxs.common.module.codegenerate.template.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Data
public class CodeGenerateContext {
    /*获取到项目的绝对路径*/
    private String absoluteDir = System.getProperty("user.dir" );
    /*基础包名*/
    private String basePackageName = "xxs.common";
    /*xml文件存放的位置,绝对路径如果为空，那么存放的位置就是根mapper接口放一块，否则就放到resources文件夹下*/
    private String mapperXmlPathName;
    /*模块名*/
    private String moduleName = "xxsxx";
    /*作者*/
    private String author = "aaa";
    //是否使用jsr303Verify
    private boolean jsr303Verify = true;
    //是否使用lombok
    private boolean lombok = true;
    //是否有swagger
    private boolean swagger = true;
    //是否 mybatisPlus
    private boolean mybatisPlus = false;
    //生成日期
    private String generateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" ).format(new Date());
    //模板集合
    private List<Template> templates = new ArrayList<>();
    //模板扩展
    private GenerateFilterContext generateFilterContext = new GenerateFilterContext();
    // 如下的配置是提供默认提供的配置
    private DataSourceConfig dataSourceConfig = new DataSourceConfig();
    private ControllerTemplateConfig controllerConfig = new ControllerTemplateConfig(this);
    private EntityTemplateConfig entityConfig = new EntityTemplateConfig(this);
    private MapperInterfaceTemplateConfig mapperInterfaceConfig = new MapperInterfaceTemplateConfig(this);
    private MapperXmlTemplateConfig mapperXmlConfig = new MapperXmlTemplateConfig(this);
    private ServiceImplTemplateConfig serviceImplConfig = new ServiceImplTemplateConfig(this);
    private ServiceInterfaceTemplateConfig serviceInterfaceConfig = new ServiceInterfaceTemplateConfig(this);


    public CodeGenerateContext() {
        this.initTemplate();
    }

    /*初始化默认模板*/
    public void initTemplate() {
        templates.add(new ServiceTemplate(this));
        templates.add(new ControllerTemplate(this));
        templates.add(new ServiceImplTemplate(this));
        templates.add(new MapperTemplate(this));
        templates.add(new MapperXmlTemplate(this));
        templates.add(new EntityTemplate(this));
        templates.add(new EntityTemplate(this));
    }

    public CodeGenerateContext addTemplate(Template template) {
        templates.add(template);
        return this;
    }
}
