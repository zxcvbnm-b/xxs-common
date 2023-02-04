package xxs.common.module.codegenerate.mybatisplus;

import cn.hutool.core.collection.CollectionUtil;
import xxs.common.module.codegenerate.*;
import xxs.common.module.codegenerate.config.DataSourceConfig;
import xxs.common.module.codegenerate.filter.GenerateFilterContext;
import xxs.common.module.codegenerate.model.ColumnInfo;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.model.TableRelationship;
import xxs.common.module.codegenerate.template.*;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO JDBC相关api https://www.runoob.com/manual/jdk11api/java.sql/java/sql/DatabaseMetaData.html
public class CodeGenerator {
    static VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();

    public static void main(String[] args) throws Exception {
        codeGenerator("sys_user");
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
            test(tableInfo);
            generator(codeGenerateContext, generateFilterContext, genTemplate, tableInfo);
        }
    }
    //TODO 一对多 一对一关系测试
    private static void test(TableInfo tableInfo) throws SQLException {
        Map<String, TableInfo> tableInfosMap2 = LoadTableInfo.loadTables(new DataSourceConfig(), "sys_user_role");
        TableInfo tableInfo1 = tableInfosMap2.get("sys_user_role");
        /*一对多的关系 要处理级联操作*/
        List<TableRelationship> tableRelationships=new ArrayList<>();
        TableRelationship tableRelationship =   new TableRelationship();
        tableRelationship.setRelationTable(tableInfo1);
        tableRelationship.setRelationColumnInfo(tableInfo1.getColumnInfos().get(2));
        tableRelationship.setOne2One(true);
        tableRelationships.add(tableRelationship);
        tableInfo.setTableRelationships(tableRelationships);
    }

    private static void generator(CodeGenerateContext codeGenerateContext, GenerateFilterContext generateFilterContext, List<Template> genTemplate, TableInfo tableInfo) throws Exception {
        List<TableRelationship> tableRelationships = tableInfo.getTableRelationships();
        if (CollectionUtil.isNotEmpty(tableRelationships)) {
            /*TODO 需要构造关系 递归生成关系--start*/
            for (TableRelationship tableRelationship : tableRelationships) {
               generator(codeGenerateContext, generateFilterContext, genTemplate, tableRelationship.getRelationTable());
            }
            /*递归生成关系--end*/
        }
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
