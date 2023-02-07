package xxs.common.module.codegenerate.template;

import org.apache.commons.lang3.StringUtils;
import xxs.common.module.codegenerate.CodeGenerateContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTemplate implements Template {
    protected CodeGenerateContext codeGenerateContext;
    //src/main/java目录
    protected final static String SRC_MAIN_JAVA_PATH=File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
    //src/main/resources目录
    protected final static String SRC_MAIN_RESOURCES_PATH=File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
    //src/test/java目录
    protected final static String SRC_TEST_JAVA_PATH=File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator;
    //src/test/resources目录
    protected final static String SRC_TEST_RESOURCES_PATH=File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    private boolean genTest=true;
    protected final static String JAVA_FILE_POST=".java";
    protected final static String XML_FILE_POST=".xml";
    public AbstractTemplate(CodeGenerateContext codeGenerateContext) {
        this.codeGenerateContext = codeGenerateContext;
    }

    @Override
    public Map<String, Object> getObjectValueMap() {
        Map<String, Object> objectValueMap = new HashMap<>();
        return objectValueMap;
    }
    /**
     * 获取文件名生成
     */
    protected String getFileName(String javaFileName, String basePackageName, String moduleName, String simplePackageName) {
        String packagePath = SRC_MAIN_JAVA_PATH;
        if(genTest){
            packagePath = SRC_TEST_JAVA_PATH;
        }
        if (StringUtils.isNotBlank(basePackageName)) {
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
    protected String getResourceFileName(String javaFileName,String basePackageName, String moduleName, String simplePackageName) {
        String packagePath = SRC_MAIN_RESOURCES_PATH;
        if(genTest){
            packagePath = SRC_TEST_RESOURCES_PATH;
        }
        if (StringUtils.isNotBlank(basePackageName)) {
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
     * 获取文件名
     */
    protected String getResourceFileName(String javaFileName, String simplePackageName) {
        String packageName = codeGenerateContext.getBasePackageName();
        String moduleName = codeGenerateContext.getModuleName();
        return getResourceFileName(javaFileName, packageName, moduleName, simplePackageName);

    }
    /**
     * 获取文件名
     */
    protected String getFileName(String javaFileName, String simplePackageName) {
        String packageName = codeGenerateContext.getBasePackageName();
        String moduleName = codeGenerateContext.getModuleName();
        return getFileName(javaFileName, packageName, moduleName, simplePackageName);
    }
}
