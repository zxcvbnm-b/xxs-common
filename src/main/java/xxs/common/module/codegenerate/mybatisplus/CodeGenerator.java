package xxs.common.module.codegenerate.mybatisplus;

import xxs.common.module.codegenerate.*;
import xxs.common.module.codegenerate.config.DataSourceConfig;
import xxs.common.module.codegenerate.filter.GenerateFilterContext;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.*;

import java.io.File;
import java.util.List;
import java.util.Map;
//TODO
public class CodeGenerator {
    static VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();


    public static void main(String[] args) throws Exception {
        codeGenerator("bus");
    }

    public static void codeGenerator(String tables) throws Exception {
        CodeGenerateContext codeGenerateContext = new CodeGenerateContext();
        codeGenerator(codeGenerateContext, tables);
    }

    public static void codeGenerator(CodeGenerateContext codeGenerateContext, String tables) throws Exception {
        //执行之前的拦截功能扩展
        GenerateFilterContext generateFilterContext = codeGenerateContext.getGenerateFilterContext();
        generateFilterContext.init(codeGenerateContext);
        List<Template> genTemplate = codeGenerateContext.getTemplates();
        Map<String, TableInfo> tableInfosMap = LoadTableInfo.loadTables(new DataSourceConfig(), tables);
        for (String tableInfoMapKey : tableInfosMap.keySet()) {
            TableInfo tableInfo = tableInfosMap.get(tableInfoMapKey);
            //遍历表时
            generateFilterContext.tableExePre(codeGenerateContext, tableInfo);
            VelocityParamBuilder velocityParamBuilder = new VelocityParamBuilder(codeGenerateContext);
            Map<String, Object> objectValueMap = velocityParamBuilder.put(Constants.TABLE_INFO_KEY_NAME, tableInfo).get();
            //初始化参数
            for (Template template : genTemplate) {
                //模板执行之前的扩展
                generateFilterContext.templateExePre(codeGenerateContext, tableInfo, template);
                Map<String, Object> params = template.getObjectValueMap();
                objectValueMap.putAll(params);
            }
            //真正执行
            for (Template template : genTemplate) {
                String outFilePathName = template.getOutFilePathName(tableInfo);
                String templateFilePathName = template.getTemplateFilePathName();
                Map<String, Object> params = template.getObjectValueMap();
                objectValueMap.putAll(params);
                velocityTemplateEngine.generate(objectValueMap, templateFilePathName, new File(outFilePathName));
            }
        }
    }
}
