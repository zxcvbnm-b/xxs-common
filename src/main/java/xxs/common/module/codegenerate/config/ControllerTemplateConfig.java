package xxs.common.module.codegenerate.config;

import lombok.Getter;
import lombok.Setter;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.utils.other.XxsProperties;

/**
 * @author xxs
 */
@Setter
@Getter
public class ControllerTemplateConfig extends AbstractTemplateConfig {
    /**
     * 控制器文件名称
     */
    private String controllerName;
    /**
     * 控制器请求根路径名称
     */
    private String controllerPathName;

    public ControllerTemplateConfig(XxsProperties properties) {
        super(properties.getString(Constants.CONTROLLER_PACKAGE_SIMPLE_NAME_PROPERTY_NAME, Constants.DEFAULT_CONTROLLER_PACKAGE_SIMPLE_NAME),
                properties.getString(Constants.CONTROLLER_FILE_POST_PROPERTY_NAME, Constants.DEFAULT_CONTROLLER_FILE_POST),
                Constants.VELOCITY_PARAM_CONTROLLER_CONFIG_NAME);
    }

}
