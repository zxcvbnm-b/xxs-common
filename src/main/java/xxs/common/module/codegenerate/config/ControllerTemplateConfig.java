package xxs.common.module.codegenerate.config;

import lombok.Data;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.Constants;

/**
 * @author xxs
 */
@Data
public class ControllerTemplateConfig extends AbstractTemplateConfig {
    /**
     * 是否是rest风格
     */
    private boolean restControllerStyle = true;
    /**
     * 控制器文件名称
     */
    private String controllerName;
    /**
     * 控制器请求根路径名称
     */
    private String controllerPathName;
    /**
     * 响应公共类
     */
    private Class responseClass;

    public ControllerTemplateConfig() {
        this.filePost = Constants.DEFAULT_CONTROLLER_FILE_POST;
        this.packageSimpleName = Constants.DEFAULT_CONTROLLER_PACKAGE_SIMPLE_NAME;
    }

}
