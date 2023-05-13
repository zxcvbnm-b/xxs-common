package xxs.common.module.codegenerate.config;

import lombok.Data;
/**
 * 抽象的模板配置
 *
 * @author xxs
 */
@Data
public abstract class AbstractTemplateConfig {
    /**
     * 基础包名(不是全包名)
     */
    protected String packageSimpleName = "";
    /**
     * 文件后缀名
     */
    protected String filePost = "";

    /**
     * 配置名称
     */
    protected String configName = "";

    public AbstractTemplateConfig(String configName) {
        this.configName = configName;
    }

    public AbstractTemplateConfig(String packageSimpleName, String filePost, String configName) {
        this.packageSimpleName = packageSimpleName;
        this.filePost = filePost;
        this.configName = configName;
    }

}
