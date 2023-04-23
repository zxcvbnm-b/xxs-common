package xxs.common.module.codegenerate;

import lombok.Data;
import xxs.common.module.codegenerate.config.*;
import xxs.common.module.codegenerate.filter.GenerateFilterContext;
import xxs.common.module.codegenerate.filter.IGenerateFilter;
import xxs.common.module.codegenerate.template.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 代码生成的基本上下文（参数，默认配置）---基本配置 ，配置应该是在运行前完成属性修改的。可以整合springboot的自动配置
 *
 * @author xxs
 */
@Data
public class CodeGenerateContext {
    /**
     * 获取到项目的绝对路径
     */
    private String absoluteDir = System.getProperty("user.dir");
    /**
     * 是否生成到测试模块下 --test/java/
     */
    private boolean genToTestModule = true;
    /**
     * 是否覆盖生成的文件 如果不覆盖，那么使用时间来做唯一标识 （文件名+时间）会导致java文件和类名不一样
     */
    private boolean coverExistFile = false;
    /**
     * 基础包名
     */
    private String basePackageName = "xxs.common";
    /**
     * TODO 去除表前缀问题未修复
     */
    private String tablePre = "";
    /**
     * xml文件存放的位置,绝对路径如果为空，那么存放的位置就是跟mapper接口放一块，否则就放到resources文件夹下
     */
    private String mapperXmlPathName;
    /**
     * 模块名
     */
    private String moduleName = "xxsxx";
    /**
     * 作者
     */
    private String author = "xxs";
    /**
     * 是否使用jsr303Verify
     */

    private boolean jsr303Verify = true;
    /**
     * 是否使用lombok
     */
    private boolean lombok = true;
    /**
     * 是否有swagger
     */
    private boolean swagger = true;
    /**
     * 逻辑删除字段
     */
    private String deletedColumn;
    /**
     * 乐观锁列
     */
    private String versionColumn;
    /**
     * 插入时自动插入值字段-mybatisplus使用
     */
    private List<String> insertFillColumn;
    /**
     * 更新时自动插入值字段-mybatisplus使用
     */
    private List<String> updateFillColumn;
    /**
     * 是否 mybatisPlus
     */
    private boolean mybatisPlus = true;
    /**
     * 是否 生成服务类验证方法，如deleteValid 删除的验证方法
     */
    private boolean genValidMethod = true;
    /**
     * 生成日期
     */
    private String generateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    /**
     * 模板集合
     */
    private List<Template> templates = new ArrayList<>();
    /**
     * 模板扩展
     */
    private GenerateFilterContext generateFilterContext = new GenerateFilterContext();
    /**
     * 模板扩展
     */
    private VelocityParamBuilder velocityParamBuilder = new VelocityParamBuilder(this);

    /**
     * 如下的配置是提供默认提供的配置
     */
    private DataSourceConfig dataSourceConfig = new DataSourceConfig();
    private ControllerTemplateConfig controllerConfig = new ControllerTemplateConfig();
    private EntityTemplateConfig entityConfig = new EntityTemplateConfig();
    private MapperInterfaceTemplateConfig mapperInterfaceConfig = new MapperInterfaceTemplateConfig();
    private MapperXmlTemplateConfig mapperXmlConfig = new MapperXmlTemplateConfig();
    private ServiceImplTemplateConfig serviceImplConfig = new ServiceImplTemplateConfig();
    private ServiceInterfaceTemplateConfig serviceInterfaceConfig = new ServiceInterfaceTemplateConfig();

    public CodeGenerateContext() {
        this.initTemplate();
    }

    /**
     * 初始化默认模板
     */
    public void initTemplate() {
        templates.add(new ServiceTemplate());
        templates.add(new ControllerTemplate());
        templates.add(new ServiceImplTemplate());
        templates.add(new MapperTemplate());
        templates.add(new MapperXmlTemplate());
        templates.add(new EntityTemplate());
    }

    public CodeGenerateContext addTemplate(Template template) {
        templates.add(template);
        return this;
    }

    public void addGenerateFilter(IGenerateFilter iGenerateFilter) {
        generateFilterContext.addGenerateFilter(iGenerateFilter);
    }
}
