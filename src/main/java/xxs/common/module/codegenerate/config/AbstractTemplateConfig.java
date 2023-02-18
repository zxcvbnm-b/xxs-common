package xxs.common.module.codegenerate.config;

import xxs.common.module.codegenerate.CodeGenerateContext;

/**
 * 抽象的模板配置
 *
 * @author xxs
 */
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
     * 代码生成的基本配置
     */
    protected CodeGenerateContext codeGenerateContext;

    public AbstractTemplateConfig(CodeGenerateContext codeGenerateContext) {
        this.codeGenerateContext = codeGenerateContext;
    }

    public AbstractTemplateConfig(String packageSimpleName, String filePost, CodeGenerateContext codeGenerateContext) {
        this.packageSimpleName = packageSimpleName;
        this.filePost = filePost;
        this.codeGenerateContext = codeGenerateContext;
    }

    public String getPackageSimpleName() {
        return packageSimpleName;
    }

    public void setPackageSimpleName(String packageSimpleName) {
        this.packageSimpleName = packageSimpleName;
    }

    public String getFilePost() {
        return filePost;
    }

    public void setFilePost(String filePost) {
        this.filePost = filePost;
    }

    public CodeGenerateContext getCodeGenerateContext() {
        return codeGenerateContext;
    }

    public void setCodeGenerateContext(CodeGenerateContext codeGenerateContext) {
        this.codeGenerateContext = codeGenerateContext;
    }
}
