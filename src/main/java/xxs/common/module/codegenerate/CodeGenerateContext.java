package xxs.common.module.codegenerate;

import com.alibaba.druid.DbType;
import lombok.Data;
import xxs.common.module.codegenerate.config.*;
import xxs.common.module.codegenerate.enums.ClassCoverMode;
import xxs.common.module.codegenerate.filter.*;
import xxs.common.module.codegenerate.template.*;
import xxs.common.module.codegenerate.velocity.VelocityParamBuilder;
import xxs.common.module.utils.other.LoadPropertyUtils;
import xxs.common.module.utils.other.XxsProperties;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 代码生成的基本上下文（参数，默认配置）---基本配置 ，配置应该是在运行前完成属性修改的。可以整合springboot的自动配置
 *
 * @author xxs
 */
@Data
public class CodeGenerateContext {
    protected XxsProperties properties = LoadPropertyUtils.copy();
    /**
     * 数据库类型
     */
    protected DbType dbType = DbType.valueOf(properties.getString(Constants.DB_TYPE_PROPERTY_NAME, "mysql"));
    /**
     * 获取到项目的绝对路径
     */
    protected String absoluteDir = properties.getString(Constants.ABSOLUTEDIR_PROPERTY_NAME, System.getProperty("user.dir"));
    /**
     * 是否生成到测试模块下 --test/java/
     */
    protected boolean genToTestModule = properties.getBoolean(Constants.GEN_TO_TEST_MODULE_PROPERTY_NAME, true);
    /**
     * 是否覆盖生成的文件 如果不覆盖，那么使用时间来做唯一标识 （文件名+时间）会导致java文件和类名不一样
     */
    protected boolean coverExistFile = properties.getBoolean(Constants.COVER_EXIST_FILE_PROPERTY_NAME, false);
    /**
     * 基础包名
     */
    protected String basePackageName = properties.getString(Constants.BASE_PACKAGE_NAME_PROPERTY_NAME, "com");
    /**
     * 去除表前缀 -- 区分大小写
     */
    protected String tablePre = properties.getString(Constants.TABLE_PRE_NAME_PROPERTY_NAME, "");
    /**
     * xml文件存放的位置,绝对路径如果为空，那么存放的位置就是跟mapper接口放一块，否则就放到resources文件夹下
     */
    protected String mapperXmlPathName = properties.getString(Constants.MAPPER_XML_PATHNAME_PROPERTY_NAME, "");
    /**
     * 模块名
     */
    protected String moduleName = properties.getString(Constants.MODULE_NAME_PROPERTY_NAME, "");
    /**
     * 作者
     */
    protected String author = properties.getString(Constants.AUTHOR_NAME_PROPERTY_NAME, "xxs");

    /**
     * 是否使用jsr303Verify
     */
    protected boolean jsr303Verify = properties.getBoolean(Constants.JSR303VERIFY_PROPERTY_NAME, true);
    /**
     * 是否使用lombok
     */
    protected boolean lombok = properties.getBoolean(Constants.LOMBOK_PROPERTY_NAME, true);
    /**
     * 是否使用swagger
     */
    protected boolean swagger = properties.getBoolean(Constants.SWAGGER_PROPERTY_NAME, true);
    /**
     * 逻辑删除列名-mybatisplus使用（未驼峰）
     */
    protected String deletedColumn = properties.getString(Constants.DELETED_COLUMN_PROPERTY_NAME);
    /**
     * 乐观锁列名-mybatisplus使用（未驼峰）
     */
    protected String versionColumn = properties.getString(Constants.VERSION_COLUMN_PROPERTY_NAME);
    /**
     * 插入时自动插入值字段-mybatisplus使用（未驼峰）
     */
    protected List<String> insertFillColumn = properties.getStringList(Constants.INSERT_FILL_COLUMN_PROPERTY_NAME);
    /**
     * 更新时自动插入值字段-mybatisplus使用（未驼峰）
     */
    protected List<String> updateFillColumn = properties.getStringList(Constants.UPDATE_FILL_COLUMN_PROPERTY_NAME);
    /**
     * 是否 mybatisPlus
     */
    protected boolean mybatisPlus = true;
    /**
     * 是否 生成服务类验证方法，如deleteValid 删除的验证方法
     */
    protected boolean genValidMethod = properties.getBoolean(Constants.GEN_VALID_METHOD_PROPERTY_NAME, true);
    /**
     * 生成日期
     */
    protected String generateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    /**
     * 模板集合 通过 模板路径
     */
    protected Set<Template> templates = new HashSet<>();
    /**
     * 模板扩展
     */
    protected GenerateFilterContext generateFilterContext = new GenerateFilterContext();
    /**
     * 模板参数处理
     */
    protected VelocityParamBuilder velocityParamBuilder = new VelocityParamBuilder(this);
    /**
     * 其他的全局配置
     */
    protected Map<String, Object> otherGlobalConfigMap = new HashMap<>();

    /**
     * 服务接口配置
     */
    protected ServiceInterfaceTemplateConfig serviceInterfaceTemplateConfig = new ServiceInterfaceTemplateConfig(properties);
    /**
     * 控制器配置
     */
    protected ControllerTemplateConfig controllerTemplateConfig = new ControllerTemplateConfig(properties);
    /**
     * 服务实现类配置
     */
    protected ServiceImplTemplateConfig serviceImplTemplateConfig = new ServiceImplTemplateConfig(properties);
    /**
     * mapper接口配置
     */
    protected MapperInterfaceTemplateConfig mapperInterfaceTemplateConfig = new MapperInterfaceTemplateConfig(properties);
    /**
     * mapperxml配置
     */
    protected MapperXmlTemplateConfig mapperXmlTemplateConfig = new MapperXmlTemplateConfig(properties);
    /**
     * 实体类模板配置
     */
    protected EntityTemplateConfig entityTemplateConfig = new EntityTemplateConfig(properties);
    protected ClassCoverMode classCoverMode = ClassCoverMode.getClassCoverModeByName(properties.getString(Constants.GEN_TO_TEST_MODULE_PROPERTY_NAME), ClassCoverMode.MERGE_FROM_OLD);

    public CodeGenerateContext() {
    }

    public CodeGenerateContext addTemplate(Template template) {
        templates.add(template);
        return this;
    }

    public void addGenerateFilter(IGenerateFilter iGenerateFilter) {
        generateFilterContext.addGenerateFilter(iGenerateFilter);
    }

}
