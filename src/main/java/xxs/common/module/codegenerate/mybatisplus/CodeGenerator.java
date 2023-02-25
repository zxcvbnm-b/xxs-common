package xxs.common.module.codegenerate.mybatisplus;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.CollectionUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO JDBC相关api https://www.runoob.com/manual/jdk11api/java.sql/java/sql/DatabaseMetaData.html
 *
 * @author
 */
public class CodeGenerator {
    static VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();

    public static void main(String[] args) throws Exception {
        //单表生成--当然也支持复杂的多表生成，需要实现 IGenerateFilter拦截器，拦截tableExePre实现功能扩展
        singleTableCodeGenerator("sys_user" );
        //多表生成 -只支持两个表生成，如果需要复杂得表关系，那么需要自己实现拦截器，修改关联关系即可。
        relationCodeGenerator("sys_user","sys_user_role","user_id",false);
    }

    public static void singleTableCodeGenerator(String tables) throws Exception {
        CodeGenerateContext codeGenerateContext = new CodeGenerateContext();
        singleTableCodeGenerator(codeGenerateContext, tables);
    }

    /**
     * 单表代码生成（不涉及一对一 一对多）
     *
     * @param codeGenerateContext 代码生成上下文
     * @param tables              表名集合
     * @throws Exception
     */
    public static void singleTableCodeGenerator(CodeGenerateContext codeGenerateContext, String tables) throws Exception {
        //执行之前的拦截功能扩展
        GenerateFilterContext generateFilterContext = codeGenerateContext.getGenerateFilterContext();
        generateFilterContext.init(codeGenerateContext);
        List<Template> genTemplate = codeGenerateContext.getTemplates();
        Map<String, TableInfo> tableInfosMap = LoadTableInfo.loadTables(new DataSourceConfig(), tables);
        for (String tableInfoMapKey : tableInfosMap.keySet()) {
            TableInfo tableInfo = tableInfosMap.get(tableInfoMapKey);
            //遍历表在执行之前时 可以扩展表对表的关联关系维护实现一对多/一对一的复杂代码生成
            generateFilterContext.tableExePre(codeGenerateContext, tableInfo);
            generator(codeGenerateContext, generateFilterContext, genTemplate, tableInfo);
        }
    }

    public static void relationCodeGenerator(String mainTableName, String relationTableName, String relationColumnName, boolean one2One) throws Exception {
        CodeGenerateContext codeGenerateContext = new CodeGenerateContext();
        relationCodeGenerator(codeGenerateContext, mainTableName, relationTableName, relationColumnName, one2One);
    }

    /**
     * 只能处理一对一对一/一对多的关系
     *
     * @param codeGenerateContext
     * @param mainTableName           主表 user
     * @param relationTableName       从表 role
     * @param relationColumnName      从表中通过哪个列关联主表，比如role中的userId关联user表
     * @param one2One             是否是一对一
     * @throws Exception
     */
    public static void relationCodeGenerator(CodeGenerateContext codeGenerateContext, String mainTableName, String relationTableName, String relationColumnName, boolean one2One) throws Exception {
        //执行之前的拦截功能扩展
        GenerateFilterContext generateFilterContext = codeGenerateContext.getGenerateFilterContext();
        generateFilterContext.init(codeGenerateContext);
        List<Template> genTemplate = codeGenerateContext.getTemplates();
        Map<String, TableInfo> tableInfosMap = LoadTableInfo.loadTables(new DataSourceConfig(), mainTableName);
        for (String tableInfoMapKey : tableInfosMap.keySet()) {
            TableInfo tableInfo = tableInfosMap.get(tableInfoMapKey);
            //遍历表在执行之前时 可以扩展表对表的关联关系维护实现一对多/一对一的复杂代码生成
            generateFilterContext.tableExePre(codeGenerateContext, tableInfo);
            if (tableInfoMapKey.equalsIgnoreCase(mainTableName)) {
                buildRelation(tableInfo, relationTableName, relationColumnName, one2One);
            }
            generator(codeGenerateContext, generateFilterContext, genTemplate, tableInfo);
        }
    }

    //TODO 一对多 一对一关系测试
    private static void buildRelation(TableInfo mainTable, String relationTableName, String relationColumn, boolean one2One) throws SQLException {
        Map<String, TableInfo> tableInfosMap = LoadTableInfo.loadTables(new DataSourceConfig(), relationTableName);
        TableInfo relationTable = tableInfosMap.get(relationTableName);
        if (relationTableName == null || relationColumn == null) {
            throw new RuntimeException("关联表需要在tables中出现并且数据库中存在！" );
        }
        /*一对多的关系 要处理级联操作*/
        List<TableRelationship> tableRelationships = new ArrayList<>();
        TableRelationship tableRelationship = new TableRelationship();
        Map<String, List<ColumnInfo>> columnInfoMap = relationTable.getColumnInfos().stream().collect(Collectors.groupingBy(ColumnInfo::getColumnName));
        tableRelationship.setRelationTable(relationTable);
        List<ColumnInfo> columnInfoList = columnInfoMap.get(relationColumn);
        if (CollectionUtils.isEmpty(columnInfoList)) {
            throw new RuntimeException("关联列需要在关联表中存在！" );
        }
        ColumnInfo relationColumnInfo = columnInfoList.get(0);
        tableRelationship.setRelationColumnInfo(relationColumnInfo);
        tableRelationship.setOne2One(one2One);
        tableRelationships.add(tableRelationship);
        TableRelationship tableRelationshipMainTableInfo = new TableRelationship();
        tableRelationshipMainTableInfo.setRelationTable(mainTable);
        tableRelationshipMainTableInfo.setRelationColumnInfo(relationColumnInfo);
        tableRelationshipMainTableInfo.setOne2One(one2One);
        relationTable.setTableRelationshipMainTableInfo(tableRelationshipMainTableInfo);
        mainTable.setTableRelationships(tableRelationships);
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
            String realOutFilePathName = getRealOutFilePathName(outFilePathName, codeGenerateContext);
            String templateFilePathName = template.getTemplateFilePathName();
            Map<String, Object> params = template.getObjectValueMap();
            objectValueMap.putAll(params);
            velocityTemplateEngine.generate(objectValueMap, templateFilePathName, new File(realOutFilePathName));
        }
    }

    /**
     * 获取真正的输出文件名称  如果文件存在，那么添加日期作为文件标识 （这样会导致java文件和类名不一样）
     */
    private static String getRealOutFilePathName(String outFilePathName, CodeGenerateContext codeGenerateContext) {
        String realOutFilePathName = outFilePathName;
        if (codeGenerateContext.isCoverExistFile()) {
            return realOutFilePathName;
        }
        File outFile = new File(realOutFilePathName);
        if (outFile.exists()) {
            String fileName = outFilePathName.substring(outFilePathName.lastIndexOf("\\" ) + 1, outFilePathName.lastIndexOf("." ));
            String filePost = outFilePathName.substring(outFilePathName.lastIndexOf("." ) + 1);
            String filePre = outFilePathName.substring(0, outFilePathName.lastIndexOf("\\" ) + 1);
            String newFileName = fileName + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH-mm-ss" );
            realOutFilePathName = filePre + newFileName + "." + filePost;
        }
        return realOutFilePathName;
    }

}
