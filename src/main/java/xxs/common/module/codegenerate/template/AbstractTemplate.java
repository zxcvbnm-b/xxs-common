package xxs.common.module.codegenerate.template;

import org.apache.commons.lang3.StringUtils;
import xxs.common.module.codegenerate.CodeGenerateContext;

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

    public AbstractTemplate() {
    }

    @Override
    public Map<String, Object> getObjectValueMap() {
        Map<String, Object> objectValueMap = new HashMap<>();
        return objectValueMap;
    }

    /**
     * 获取文件名生成
     */
    protected String getFileName(CodeGenerateContext codeGenerateContext, String javaFileName, String simplePackageName) {
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
        if (StringUtils.isNotBlank(simplePackageName)) {
            packagePath += File.separator + simplePackageName.replace(".", File.separator) + File.separator;
        }
        return codeGenerateContext.getAbsoluteDir() + packagePath + javaFileName;
    }

    /**
     * 获取文件名生成到main/resources
     */
    protected String getResourceFileName(CodeGenerateContext codeGenerateContext, String fileName, String simplePackageName) {
        String packagePath = SRC_MAIN_RESOURCES_PATH;
        if (codeGenerateContext.isGenToTestModule()) {
            packagePath = SRC_TEST_RESOURCES_PATH;
        }
        if (StringUtils.isNotBlank(simplePackageName)) {
            packagePath += File.separator + simplePackageName.replace(".", File.separator) + File.separator;
        }
        return codeGenerateContext.getAbsoluteDir() + packagePath + fileName;
    }
}
