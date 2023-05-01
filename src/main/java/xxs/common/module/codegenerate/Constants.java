package xxs.common.module.codegenerate;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.regex.Pattern;

/**
 * 常量类
 *
 * @author xxs
 */
public interface Constants {
    /**
     * 控制器后缀
     */
    String DEFAULT_CONTROLLER_FILE_POST = "Controller";
    /**
     * 控制器基本包名
     */
    String DEFAULT_CONTROLLER_PACKAGE_SIMPLE_NAME = "controller";

    /**
     * 实体类文件名后缀
     */
    String DEFAULT_ENTITY_FILE_POST = "Entity";
    /**
     * 实体类文件名后缀
     */
    String DEFAULT_PARAM_FILE_POST = "Param";
    /**
     * 实体类文件名后缀
     */
    String DEFAULT_RESULT_FILE_POST = "Result";
    /**
     * 实体类存放的基本包名
     */
    String DEFAULT_ENTITY_PACKAGE_SIMPLE_NAME = "model.entity";

    /**
     * mapper接口文件名后缀
     */
    String DEFAULT_MAPPER_INTERFACE_FILE_POST = "Mapper";
    /**
     * mapper接口存放的基本包名
     */
    String DEFAULT_MAPPER_INTERFACE_PACKAGE_SIMPLE_NAME = "mapper";

    /**
     * mapper.xml文件接口文件名后缀
     */
    String DEFAULT_MAPPER_FILE_POST = "Mapper";
    /**
     * mapper.xml接口存放的基本包名
     */
    String DEFAULT_MAPPER_PACKAGE_SIMPLE_NAME = "mapper";

    /**
     * 服务实现类文件后缀名
     */
    String DEFAULT_SERVICE_IMPL_FILE_POST = "ServiceImpl";
    /**
     * 服务实现类包名
     */
    String DEFAULT_SERVICE_IMPL_PACKAGE_SIMPLE_NAME = "service.impl";

    /**
     * 服务接口后缀名
     */
    String DEFAULT_SERVICE_INTERFACE_FILE_POST = "Service";
    /**
     * 服务实现类基本包名
     */
    String DEFAULT_SERVICE_INTERFACE_PACKAGE_SIMPLE_NAME = "service";
    /**
     * 模板参数表信息的属性名称
     */
    String TABLE_INFO_KEY_NAME = "tableInfo";
    /**
     * 基础模板配置类属性名称
     */
    String VELOCITY_PARAM_CONTROLLER_CONFIG_NAME = "controllerConfig";
    String VELOCITY_PARAM_ENTITY_CONFIG_NAME = "entityConfig";
    String VELOCITY_PARAM_MAPPER_INTERFACE_CONFIG_NAME = "mapperInterfaceConfig";
    String VELOCITY_PARAM_MAPPER_XML_CONFIG_NAME = "mapperXmlConfig";
    String VELOCITY_PARAM_SERVICE_IMPL_CONFIG_NAME = "serviceImplConfig";
    String VELOCITY_PARAM_SERVICE_INTERFACE_CONFIG_NAME = "serviceInterfaceConfig";
    /**
     * 判断sql是否是一个查询语句
     */
    Pattern SELECT_SQL_PATTERN = Pattern.compile("(?<!\\w)(?i)(INSERT|DELETE|TRUNCATE|UPDATE|CREATE|ALTER|DROP)(?!\\w)");
    String SQL_PARTITION = ";";

    PropertyPlaceholderHelper PLACEHOLDER_HELPER = new PropertyPlaceholderHelper("${", "}");

    String TABLE_ALIAS_PLACEHOLDER = "tableAlias";

    String COMMA_SEPARATOR = ",";
}
