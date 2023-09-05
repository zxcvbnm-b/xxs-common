package xxs.common.module.codegenerate.mybatisplus;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.util.CollectionUtils;
import xxs.common.module.codegenerate.*;
import xxs.common.module.codegenerate.filter.GenerateFilterContext;
import xxs.common.module.codegenerate.model.ColumnInfo;
import xxs.common.module.codegenerate.model.RelationTableInfo;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.model.TableRelationship;
import xxs.common.module.codegenerate.template.*;
import xxs.common.module.codegenerate.velocity.LocalFileGenerateOutPut;
import xxs.common.module.codegenerate.velocity.VelocityParamBuilder;
import xxs.common.module.codegenerate.velocity.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JDBC相关api https://www.runoob.com/manual/jdk11api/java.sql/java/sql/DatabaseMetaData.html
 *
 * @author
 */
//TODO 2 把公共代码抽象出来 作为抽象类吧
//TODO 3 没有主键的情况下 没有主键不能生成 而且只支持单主键
public class DefaultCodeGenerator implements CodeGenerator {
    private VelocityTemplateEngine velocityTemplateEngine = VelocityTemplateEngine.getVelocityTemplateEngineInstance();
    private TableService tableService;
    private CodeGenerateContext codeGenerateContext;

    public DefaultCodeGenerator(TableService tableService) {
        this.tableService = tableService;
        this.codeGenerateContext = new ClassCodeGenerateContext().initClassCodeGenerateContext();
    }

    public DefaultCodeGenerator(TableService tableService, CodeGenerateContext codeGenerateContext) {
        this.tableService = tableService;
        this.codeGenerateContext = codeGenerateContext;
    }

    public static void main(String[] args) throws Exception {
        DefaultCodeGenerator defaultCodeGenerator = new DefaultCodeGenerator(new DBTableServiceImpl(DefaultDataSourceProvider.getDataSourceInstance()));
        //1.单表生成--当然也支持复杂的多表生成，需要实现 IGenerateFilter拦截器，拦截tableExePre实现功能扩展
//        defaultCodeGenerator.singleTableCodeGenerator("country");
        //2.多表生成 -只支持两个表生成，如果需要复杂得表关系，那么需要自己实现拦截器，修改关联关系即可。
//        defaultCodeGenerator.relationCodeGenerator("sys_user", "sys_user_role", "user_id", false);

        //3.多表生成 关联关系应该换成对象来处理 一个表和多个表的关联关系
        List<RelationTableInfo> relationTableInfos = new ArrayList<>();
        RelationTableInfo relationTableInfo2 = new RelationTableInfo();
        relationTableInfo2.setRelationTableName("city");
        relationTableInfo2.setOne2One(false);
        relationTableInfo2.setRelationColumnName("CountryCode");
        relationTableInfo2.setRelationUniqueColumnName("CountryCode");
        relationTableInfos.add(relationTableInfo2);

        RelationTableInfo relationTableInfo3 = new RelationTableInfo();
        relationTableInfo3.setRelationTableName("countrylanguage");
        relationTableInfo3.setOne2One(true);
        relationTableInfo3.setRelationUniqueColumnName("CountryCode");
        relationTableInfo3.setRelationColumnName("CountryCode");
        relationTableInfos.add(relationTableInfo3);
        defaultCodeGenerator.relationCodeGenerator("country", relationTableInfos);

    }

    /**
     * 单表代码生成（不涉及一对一 一对多）
     *
     * @param tables 表名集合
     * @throws Exception
     */
    @Override
    public void singleTableCodeGenerator(String tables) throws Exception {
        //执行之前的拦截功能扩展
        GenerateFilterContext generateFilterContext = codeGenerateContext.getGenerateFilterContext();
        generateFilterContext.init(codeGenerateContext);
        Set<Template> genTemplate = codeGenerateContext.getTemplates();
        Map<String, TableInfo> tableInfosMap = tableService.loadTables(tables, codeGenerateContext.getTablePre());
        for (String tableInfoMapKey : tableInfosMap.keySet()) {
            TableInfo tableInfo = tableInfosMap.get(tableInfoMapKey);
            //遍历表在执行之前时 可以扩展表对表的关联关系维护实现一对多/一对一的复杂代码生成
            generateFilterContext.tableExePre(codeGenerateContext, tableInfo);
            generator(codeGenerateContext, generateFilterContext, genTemplate, tableInfo);
        }
    }

    /**
     * 只能处理一对一对一/一对多的关系
     *
     * @param mainTableName      主表 user
     * @param relationTableName  从表 role
     * @param relationColumnName 从表中通过哪个列关联主表，比如role中的userId关联user表
     * @param one2One            是否是一对一
     * @throws Exception
     */
    public void relationCodeGenerator(String mainTableName, String relationTableName, String relationColumnName, String relationUniqueColumnName, boolean one2One) throws Exception {
        //执行之前的拦截功能扩展
        GenerateFilterContext generateFilterContext = codeGenerateContext.getGenerateFilterContext();
        generateFilterContext.init(codeGenerateContext);
        Set<Template> genTemplate = codeGenerateContext.getTemplates();
        Map<String, TableInfo> tableInfosMap = tableService.loadTables(mainTableName, codeGenerateContext.getTablePre());
        for (String tableInfoMapKey : tableInfosMap.keySet()) {
            TableInfo tableInfo = tableInfosMap.get(tableInfoMapKey);
            //遍历表在执行之前时 可以扩展表对表的关联关系维护实现一对多/一对一的复杂代码生成
            generateFilterContext.tableExePre(codeGenerateContext, tableInfo);
            if (tableInfoMapKey.equalsIgnoreCase(mainTableName)) {
                buildRelation(tableInfo, relationTableName, relationColumnName, relationUniqueColumnName, one2One);
            }
            generator(codeGenerateContext, generateFilterContext, genTemplate, tableInfo);
        }
    }

    /**
     * 一对多 一对一关系生成
     *
     * @param mainTable            被关联表
     * @param relationTableName    关联表名
     * @param relationColumn       关联列名
     * @param relationUniqueColumn 关联唯一列名
     * @param one2One              是否是一对一
     * @throws Exception
     */
    private void buildRelation(TableInfo mainTable, String relationTableName, String relationColumn, String relationUniqueColumn, boolean one2One) throws Exception {
        Map<String, TableInfo> tableInfosMap = tableService.loadTables(relationTableName, codeGenerateContext.getTablePre());
        TableInfo relationTable = tableInfosMap.get(relationTableName);
        if (relationTableName == null || relationColumn == null) {
            throw new RuntimeException("关联表需要在tables中出现并且数据库中存在！");
        }
        /*一对多的关系 要处理级联操作*/
        List<TableRelationship> tableRelationships = mainTable.getTableRelationships();
        if (CollectionUtils.isEmpty(tableRelationships)) {
            tableRelationships = new ArrayList<>();
        }
        TableRelationship tableRelationship = new TableRelationship();
        Map<String, List<ColumnInfo>> columnInfoMap = relationTable.getColumnInfos().stream().collect(Collectors.groupingBy(ColumnInfo::getColumnName));
        tableRelationship.setRelationTable(relationTable);
        ColumnInfo relationColumnInfo = getColumnInfo(relationColumn, columnInfoMap);
        ColumnInfo relationUniqueColumnInfo = getColumnInfo(relationUniqueColumn, columnInfoMap);
        tableRelationship.setRelationColumnInfo(relationColumnInfo);
        tableRelationship.setOne2One(one2One);
        tableRelationships.add(tableRelationship);
        TableRelationship tableRelationshipMainTableInfo = new TableRelationship();
        tableRelationshipMainTableInfo.setRelationTable(mainTable);
        tableRelationshipMainTableInfo.setRelationColumnInfo(relationColumnInfo);
        tableRelationshipMainTableInfo.setRelationUniqueColumnInfo(relationUniqueColumnInfo);
        tableRelationshipMainTableInfo.setOne2One(one2One);
        relationTable.setTableRelationshipMainTableInfo(tableRelationshipMainTableInfo);
        mainTable.setTableRelationships(tableRelationships);
    }

    private ColumnInfo getColumnInfo(String relationColumn, Map<String, List<ColumnInfo>> columnInfoMap) {
        List<ColumnInfo> columnInfoList = columnInfoMap.get(relationColumn);
        if (CollectionUtils.isEmpty(columnInfoList)) {
            throw new RuntimeException("关联列需要在关联表中存在！");
        }
        ColumnInfo relationColumnInfo = columnInfoList.get(0);
        return relationColumnInfo;
    }

    /**
     * 只能处理一对一对一/一对多的关系  多个从表
     *
     * @param mainTableName      主表 user
     * @param relationTableInfos 从表 关系
     * @throws Exception
     */
    @Override
    public void relationCodeGenerator(String mainTableName, List<RelationTableInfo> relationTableInfos) throws Exception {
        if (CollectionUtils.isEmpty(relationTableInfos)) {
            return;
        }
        //执行之前的拦截功能扩展
        GenerateFilterContext generateFilterContext = codeGenerateContext.getGenerateFilterContext();
        generateFilterContext.init(codeGenerateContext);
        Set<Template> genTemplate = codeGenerateContext.getTemplates();
        Map<String, TableInfo> tableInfosMap = tableService.loadTables(mainTableName, codeGenerateContext.getTablePre());
        for (String tableInfoMapKey : tableInfosMap.keySet()) {
            TableInfo tableInfo = tableInfosMap.get(tableInfoMapKey);
            //遍历表在执行之前时 可以扩展表对表的关联关系维护实现一对多/一对一的复杂代码生成
            generateFilterContext.tableExePre(codeGenerateContext, tableInfo);
            if (tableInfoMapKey.equalsIgnoreCase(mainTableName)) {
                buildRelation(tableInfo, relationTableInfos);
            }
            generator(codeGenerateContext, generateFilterContext, genTemplate, tableInfo);
        }
    }

    // 一对多 一对一关系  多个从表
    private void buildRelation(TableInfo mainTable, List<RelationTableInfo> relationTableInfos) throws Exception {
        if (CollectionUtils.isEmpty(relationTableInfos)) {
            return;
        }
        for (RelationTableInfo relationTableInfo : relationTableInfos) {
            buildRelation(mainTable, relationTableInfo.getRelationTableName(), relationTableInfo.getRelationColumnName(), relationTableInfo.getRelationUniqueColumnName(), relationTableInfo.isOne2One());
        }
    }

    private void generator(CodeGenerateContext codeGenerateContext, GenerateFilterContext generateFilterContext, Set<Template> genTemplate, TableInfo tableInfo) throws Exception {
        List<TableRelationship> tableRelationships = tableInfo.getTableRelationships();
        if (CollectionUtil.isNotEmpty(tableRelationships)) {
            /*需要构造关系 递归先生成相关关联表--start*/
            for (TableRelationship tableRelationship : tableRelationships) {
                generator(codeGenerateContext, generateFilterContext, genTemplate, tableRelationship.getRelationTable());
            }
            /*递归生成关系--end*/
        }
        VelocityParamBuilder velocityParamBuilder = codeGenerateContext.getVelocityParamBuilder();
        velocityParamBuilder.put(Constants.TABLE_INFO_KEY_NAME, tableInfo);
        //初始化参数
        for (Template template : genTemplate) {
            //模板执行之前的扩展
            generateFilterContext.templateExePre(codeGenerateContext, tableInfo, template);
            //整合所有的自定义模板参数，使得当前模板可以使用其他模板的参数
            Map<String, Object> params = template.customTemplateParamMap(null);
            velocityParamBuilder.putAll(params);
        }
        Map<String, Object> velocityParam = velocityParamBuilder.get();
        //真正执行代码生成
        for (Template template : genTemplate) {
            String outFilePathName = template.getOutFilePathName(codeGenerateContext, tableInfo);
            String templateFilePathName = template.getTemplateFilePathName();
            LocalFileGenerateOutPut localFileGenerateOutPut = new LocalFileGenerateOutPut(outFilePathName, false, codeGenerateContext.isCoverExistFile());
            velocityTemplateEngine.generate(localFileGenerateOutPut, velocityParam, templateFilePathName, outFilePathName);
        }
    }
}
