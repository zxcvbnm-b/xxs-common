package xxs.common.module.codegenerate.template;

import org.apache.commons.lang3.StringUtils;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.config.AbstractTemplateConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 抽象模板
 *
 * @author xxs
 */
public abstract class AbstractTemplate implements Template {
    /**
     * src/main/java目录
     */
    protected final static String SRC_MAIN_JAVA_PATH = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
    /**
     * src/main/resources目录
     */
    protected final static String SRC_MAIN_RESOURCES_PATH = File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
    /**
     * src/test/java目录
     */
    protected final static String SRC_TEST_JAVA_PATH = File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator;
    /**
     * src/test/resources目录
     */
    protected final static String SRC_TEST_RESOURCES_PATH = File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    /**
     * java文件后缀
     */
    protected final static String JAVA_FILE_POST = ".java";
    /**
     * xml文件后缀
     */
    protected final static String XML_FILE_POST = ".xml";
    protected AbstractTemplateConfig abstractTemplateConfig;


    public AbstractTemplate(AbstractTemplateConfig abstractTemplateConfig) {
        this.abstractTemplateConfig = abstractTemplateConfig;
    }

    public abstract String getFileSuffix();

    @Override
    public Map<String, Object> getObjectValueMap() {
        Map<String, Object> objectValueMap = new HashMap<>(8);
        if (abstractTemplateConfig != null) {
            objectValueMap.put(abstractTemplateConfig.getConfigName(), this.abstractTemplateConfig);
        }
        return objectValueMap;
    }

    /**
     * 获取文件名生成
     */
    protected String getFileName(CodeGenerateContext codeGenerateContext, String javaFileName) {
        String basePackageName = codeGenerateContext.getBasePackageName();
        String moduleName = codeGenerateContext.getModuleName();
        String packagePath = SRC_MAIN_JAVA_PATH;
        if (codeGenerateContext.isGenToTestModule()) {
            packagePath = SRC_TEST_JAVA_PATH;
        }
        if (StringUtils.isNotBlank(codeGenerateContext.getBasePackageName())) {
            packagePath += basePackageName.replace(".", File.separator);
        }
        if (StringUtils.isNotBlank(moduleName)) {
            packagePath += File.separator + moduleName + File.separator;
        }
        //实体包名
        if (abstractTemplateConfig != null && StringUtils.isNotBlank(abstractTemplateConfig.getPackageSimpleName())) {
            packagePath += File.separator + abstractTemplateConfig.getPackageSimpleName().replace(".", File.separator) + File.separator;
        }
        //类名后缀
        String filePathName = codeGenerateContext.getAbsoluteDir() + packagePath + javaFileName;
        if (StringUtils.isNotBlank(abstractTemplateConfig.getFilePost())) {
            filePathName = filePathName + abstractTemplateConfig.getFilePost();
        }
        //文件后缀
        if (StringUtils.isNotBlank(getFileSuffix())) {
            filePathName = filePathName + getFileSuffix();
        }
        return filePathName;
    }

    /**
     * 获取文件名生成到main/resources
     */
    protected String getResourceFileName(CodeGenerateContext codeGenerateContext, String fileName) {
        String packagePath = SRC_MAIN_RESOURCES_PATH;
        if (codeGenerateContext.isGenToTestModule()) {
            packagePath = SRC_TEST_RESOURCES_PATH;
        }
        //实体包名
        if (abstractTemplateConfig != null && StringUtils.isNotBlank(abstractTemplateConfig.getPackageSimpleName())) {
            packagePath += File.separator + abstractTemplateConfig.getPackageSimpleName().replace(".", File.separator) + File.separator;
        }
        //类名后缀
        String filePathName = codeGenerateContext.getAbsoluteDir() + packagePath + fileName;
        if (StringUtils.isNotBlank(abstractTemplateConfig.getFilePost())) {
            filePathName = filePathName + abstractTemplateConfig.getFilePost();
        }
        //文件后缀
        if (StringUtils.isNotBlank(getFileSuffix())) {
            filePathName = filePathName + getFileSuffix();
        }
        return filePathName;
    }

    @Override
    public int hashCode() {
        return this.getTemplateFilePathName().hashCode();
    }

    @Override
    public boolean equals(Object template) {
        Template templateTemp = (Template) template;
        return this.getTemplateFilePathName().equals(templateTemp.getTemplateFilePathName());
    }
}
