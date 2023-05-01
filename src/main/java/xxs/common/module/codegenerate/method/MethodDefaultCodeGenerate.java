package xxs.common.module.codegenerate.method;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.util.JdbcConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import xxs.common.module.codegenerate.*;
import xxs.common.module.codegenerate.config.DataSourceConfig;
import xxs.common.module.codegenerate.method.enums.MethodReturnType;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.enums.WhereParamNodeUseCompareType;
import xxs.common.module.codegenerate.method.model.MethodGenParamContext;
import xxs.common.module.codegenerate.method.model.MethodGenVelocityParam;
import xxs.common.module.codegenerate.method.model.UserInputWhereParam;
import xxs.common.module.codegenerate.method.model.WhereParam;
import xxs.common.module.codegenerate.method.template.MethodParamDTOTemplate;
import xxs.common.module.codegenerate.method.template.MethodResultDTOTemplate;
import xxs.common.module.codegenerate.method.whereparam.XMLWhereParamNode;
import xxs.common.module.codegenerate.method.whereparam.XmlWhereParamNodeFactory;
import xxs.common.module.codegenerate.model.SearchColumnInfo;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.Template;
import xxs.common.module.sql.DruidSqlDisposeUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * 方法级别默认代码生成
 * TODO 1 对于一个表出现多次，xml列可能无法知道需要对哪个表进行where条件过滤
 * TODO 2 生成的sql是有缺陷的，比如where条件里面 没有带表别名，因为你的sql中可能会有多个相同的字段名，不好进行匹配 除非改输入
 * TODO 3 对于一个自连接在重写sql后生成的xml sql 会出现别名不匹配的问题。
 * TODO 4 不支持union 语法的xml 列名重复重写。（不过，如果投影列中不存在列名被重写（列名重复），那么不会触发sql投影重写）
 *
 * @author xxs
 */
@Slf4j
public class MethodDefaultCodeGenerate {
    private VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
    private LoadTableService loadTableService = new LoadTableService(new DataSourceConfig());
    private CodeGenerateContext codeGenerateContext = new MethodCodeGenerateContext().initMethodCodeGenerateContext();

    public static void main(String[] args) throws Exception {
        MethodDefaultCodeGenerate methodDefaultCodeGenerate = new MethodDefaultCodeGenerate();
        Set<UserInputWhereParam> params = new HashSet<>();
        UserInputWhereParam userInputWhereParam = new UserInputWhereParam();
        userInputWhereParam.setParamType(List.class);
        userInputWhereParam.setColumnName("ID");
        userInputWhereParam.setParamName("ids");
        userInputWhereParam.setBeginParamName("beginTime");
        userInputWhereParam.setEndParamName("endTime");
        userInputWhereParam.setWhereParamNodeUseCompareType(WhereParamNodeUseCompareType.BETWEEN);
        params.add(userInputWhereParam);
        methodDefaultCodeGenerate.singleTableCodeGenerator("city", "getCity", "select * from city a inner join city b on a.ID = b.ID", params);
    }

    /**
     * @param tableName     表名，用于匹配已经存在的文件
     * @param searchName    查询名字
     * @param sql           需要处理的sql
     * @param whereParamSet 作为参数的where列
     * @throws Exception
     */
    public void singleTableCodeGenerator(String tableName, String searchName, String sql, Set<UserInputWhereParam> whereParamSet) throws Exception {
        //输入参数检查
        this.inputValueValid(tableName, sql, whereParamSet);
        MethodGenParamContext methodGenParamContext = new MethodGenParamContext();
        //需要对sql进行处理 比如列名重复，那么可能需要处理下 select 投影， 重新修改sql语句的返回列根据 searchColumnInfoBySearchSql
        methodGenParamContext.setSearchName(searchName);
        //驼峰后 首字母大写
        methodGenParamContext.setCapitalizeSearchName(StringUtils.capitalize(StrUtil.toCamelCase(searchName)));
        //获取虚拟table， 用于匹配到当前代码生成环境的文件的名称（比如用于追加方法到类中 如果存在的话）
        TableInfo tableInfo = this.getVirtualTableInfo(tableName);
        //根据sql获取sql的列（只是返回投影的列，可能在数据库中，这个列根本就不存在）的信息，ResultDTO的生成需要用到投影列，但是参数的生成需要用到表的列的
        List<SearchColumnInfo> searchColumnInfoBySearchSql = loadTableService.getSearchColumnInfoBySearchSql(sql);
        if (CollectionUtils.isEmpty(searchColumnInfoBySearchSql)) {
            log.warn("singleTableCodeGenerator not return any column info !");
            return;
        }
        //重新包装投影列,根据searchColumnInfoBySearchSql返回的值
        String realSql = this.wrapSqlProjection(sql, searchColumnInfoBySearchSql);
        methodGenParamContext.setRealSql(realSql);
        List<WhereParam> whereParamList = this.getWhereParams(whereParamSet);
        methodGenParamContext.setWhereParamList(whereParamList);
        VelocityParamBuilder velocityParamBuilder = codeGenerateContext.getVelocityParamBuilder();
        velocityParamBuilder.put(Constants.TABLE_INFO_KEY_NAME, tableInfo);
        methodGenParamContext.setAllReturnSelectList(searchColumnInfoBySearchSql);
        velocityParamBuilder.put("methodGenVelocityParam", this.initMethodGenVelocityParam(methodGenParamContext));
        Set<Template> genTemplate = codeGenerateContext.getTemplates();
        //初始化参数
        for (Template template : genTemplate) {
            Map<String, Object> params = template.customTemplateParamMap(null);
            velocityParamBuilder.putAll(params);
        }
        Map<String, Object> stringObjectMap = velocityParamBuilder.get();
        //渲染模板生成代码
        for (Template template : genTemplate) {
            velocityTemplateEngine.generate(stringObjectMap, template.getTemplateFilePathName(), this.wrapFileName(template.getOutFilePathName(codeGenerateContext, tableInfo), template, methodGenParamContext.getCapitalizeSearchName()), template.append(), false);
        }
    }

    /**
     * 如果sql存在字段重写，那么才需要重新投影 修改sql的投影字段返回
     *
     * @param sql                         输入的sql
     * @param searchColumnInfoBySearchSql 执行输入的sql后得到的返回列
     * @return
     */
    private String wrapSqlProjection(String sql, List<SearchColumnInfo> searchColumnInfoBySearchSql) {
        String realSql = sql;
        if (searchColumnInfoBySearchSql.stream().filter(item -> item.isColumnNameRewrite() == true).findAny().isPresent()) {
            //需要重写sql时  当有重复表名时，只取第一个，当sql是一个union时，也处理union
            Map<String, String> tableNameProjectionMap = new HashMap<>(10);
            Map<String, List<SearchColumnInfo>> tableColumnsListMap = searchColumnInfoBySearchSql.stream().collect(Collectors.groupingBy(SearchColumnInfo::getTableName));
            for (Map.Entry<String, List<SearchColumnInfo>> tableColumnsList : tableColumnsListMap.entrySet()) {
                List<SearchColumnInfo> value = tableColumnsList.getValue();
                String projectionString = value.stream().map(item -> String.format("${" + Constants.TABLE_ALIAS_PLACEHOLDER + "}.%s", item.getRealColumnName()) + " " + item.getColumnName()).collect(Collectors.joining(Constants.COMMA_SEPARATOR));
                tableNameProjectionMap.put(tableColumnsList.getKey(), projectionString);
            }
            realSql = DruidSqlDisposeUtils.replaceSqlProjectionByTableAlias(sql, JdbcConstants.MYSQL.name(), tableNameProjectionMap);
        }
        return realSql;
    }

    private String wrapFileName(String outFilePathName, Template template, String capitalizeSearchName) {
        if (!(template instanceof MethodParamDTOTemplate || template instanceof MethodResultDTOTemplate)) {
            return outFilePathName;
        }
        String result = outFilePathName;
        String folder = outFilePathName.substring(0, outFilePathName.lastIndexOf("\\"));
        String fileSuffix = outFilePathName.substring(outFilePathName.lastIndexOf("."));
        if (template instanceof MethodParamDTOTemplate) {
            result = folder + File.separator + capitalizeSearchName + Constants.DEFAULT_PARAM_FILE_POST + fileSuffix;
        }
        if (template instanceof MethodResultDTOTemplate) {
            result = folder + File.separator + capitalizeSearchName + Constants.DEFAULT_RESULT_FILE_POST + fileSuffix;
        }
        return result;
    }

    private MethodGenVelocityParam initMethodGenVelocityParam(MethodGenParamContext methodGenParamContext) {
        MethodGenVelocityParam methodGenVelocityParam = new MethodGenVelocityParam();
        methodGenVelocityParam.setSearchMethodName(methodGenParamContext.getSearchName());
        methodGenVelocityParam.setCapitalizeSearchMethodName(methodGenParamContext.getCapitalizeSearchName());
        //设置返回值信息
        this.setReturnTypeInfo(methodGenParamContext, methodGenVelocityParam);
        this.setMethodParamInfo(methodGenParamContext, methodGenVelocityParam);
        this.setXmlContent(methodGenParamContext, methodGenVelocityParam);
        methodGenVelocityParam.setWhereParamList(methodGenParamContext.getWhereParamList());
        methodGenVelocityParam.setReturnSelectList(methodGenParamContext.getAllReturnSelectList());
        return methodGenVelocityParam;
    }

    /**
     * 设置xml结果字符串
     *
     * @param methodGenParamContext
     */
    private void setXmlContent(MethodGenParamContext methodGenParamContext, MethodGenVelocityParam methodGenVelocityParam) {
        String realSql = methodGenParamContext.getRealSql();
        methodGenVelocityParam.setXmlContent(realSql);
        String xmlSqlContent = realSql + " where 1=1";
        List<WhereParam> whereParamList = methodGenParamContext.getWhereParamList();
        for (WhereParam whereParam : whereParamList) {
            XMLWhereParamNode xmlWhereParamNode = XmlWhereParamNodeFactory.create(whereParam, methodGenParamContext.getParamType());
            String whereParamNode = xmlWhereParamNode.getWhereParamNode();
            xmlSqlContent = xmlSqlContent + "\n" + whereParamNode;
        }
        methodGenVelocityParam.setXmlContent(xmlSqlContent);
    }

    /**
     * 设置方法参数
     *
     * @param methodGenParamContext
     * @param methodGenVelocityParam
     */
    private void setMethodParamInfo(MethodGenParamContext methodGenParamContext, MethodGenVelocityParam methodGenVelocityParam) {
        String methodParams = "";
        String methodParamNames = "";
        if (methodGenParamContext.getParamType().equals(ParamType.DTO)) {
            methodParamNames = methodGenParamContext.getSearchName() + "Param";
            methodParams = methodGenParamContext.getCapitalizeSearchName() + "Param " + methodParamNames;
            methodGenVelocityParam.setParamDTOType(true);
        } else if (methodGenParamContext.getParamType().equals(ParamType.QUERY_PARAM)) {
            List<WhereParam> whereParamList = methodGenParamContext.getWhereParamList();
            StringJoiner methodParamStringJoiner = new StringJoiner(",");
            StringJoiner methodParamNameStringJoiner = new StringJoiner(",");
            for (WhereParam whereParam : whereParamList) {
                String paramName = whereParam.getParamName();
                String camelCaseParamName = StrUtil.toCamelCase(paramName);
                //首字母大写
                String capitalizeParamName = StringUtils.capitalize(camelCaseParamName);
                methodParamNameStringJoiner.add(camelCaseParamName);
                methodParamStringJoiner.add(capitalizeParamName + " " + camelCaseParamName);
            }
            methodParamNames = methodParamNameStringJoiner.toString();
        }
        methodGenVelocityParam.setMethodParams(methodParams);
        methodGenVelocityParam.setMethodParamNames(methodParamNames);
    }

    /**
     * 设置返回值类型
     *
     * @param methodGenParamContext
     * @param methodGenVelocityParam
     */
    private void setReturnTypeInfo(MethodGenParamContext methodGenParamContext, MethodGenVelocityParam methodGenVelocityParam) {
        String returnTypeName = "";
        String returnTypeSimpleName = "";
        if (methodGenParamContext.getReturnType().equals(MethodReturnType.DTO)) {
            returnTypeName = methodGenParamContext.getCapitalizeSearchName() + "Result";
            returnTypeSimpleName = returnTypeName;
        } else if (methodGenParamContext.getReturnType().equals(MethodReturnType.LIST_DTO)) {
            returnTypeName = methodGenParamContext.getCapitalizeSearchName() + "Result";
            returnTypeSimpleName = String.format("List<%s>", returnTypeName);
        } else if (methodGenParamContext.getReturnTypeClass() != null) {
            returnTypeName = methodGenParamContext.getReturnTypeClass().getName();
            returnTypeSimpleName = methodGenParamContext.getReturnTypeClass().getSimpleName();
        }

        methodGenVelocityParam.setReturnTypeName(returnTypeName);
        methodGenVelocityParam.setReturnTypeSimpleName(returnTypeSimpleName);
    }

    private List<WhereParam> getWhereParams(Set<UserInputWhereParam> whereParamSet) {
        List<WhereParam> whereParamList = new ArrayList<>();
        //where 条件列处理
        if (!CollectionUtils.isEmpty(whereParamSet)) {
        }
        for (UserInputWhereParam userInputWhereParam : whereParamSet) {
            WhereParam whereParam = new WhereParam();
            BeanUtil.copyProperties(userInputWhereParam, whereParam);
            whereParamList.add(whereParam);
        }
        return whereParamList;
    }

    /**
     * 输入参数验证
     *
     * @param tableName
     * @param sql
     * @param whereParamSet
     */
    private void inputValueValid(String tableName, String sql, Set<UserInputWhereParam> whereParamSet) {
        if (StringUtils.isEmpty(sql)) {
            log.error("sql Can't null  null  !");
            new IllegalArgumentException("sql Can't null  !");
        }
        //判断sql是不是一个查询sql
        Matcher matcher = Constants.SELECT_SQL_PATTERN.matcher(sql);
        Assert.isTrue(!matcher.find(), "校验SQL只能是SELECT类型");
        Assert.isTrue(sql.split(Constants.SQL_PARTITION).length == 1, "只支持一条SQL语句");
        Assert.isTrue(DruidSqlDisposeUtils.validSql(sql, JdbcConstants.MYSQL.name()), "druid 校验sql出错！");
        Assert.isTrue(StringUtils.isNotEmpty(tableName), "tableName Can't null  !");
        if (CollectionUtil.isNotEmpty(whereParamSet)) {
            for (UserInputWhereParam userInputWhereParam : whereParamSet) {
                Assert.isTrue(StringUtils.isNotEmpty(userInputWhereParam.getParamName()), "paramName Can't null  !");
                Class<?> paramType = userInputWhereParam.getParamType();
                Assert.isTrue(userInputWhereParam.getParamType() != null, "paramType Can't   null  !");
                WhereParamNodeUseCompareType whereParamNodeUseCompareType = userInputWhereParam.getWhereParamNodeUseCompareType();
                Assert.isTrue(whereParamNodeUseCompareType != null, "whereParamNodeUseCompareType Can't null  !");
                if (whereParamNodeUseCompareType.equals(WhereParamNodeUseCompareType.BETWEEN)) {
                    Assert.isTrue(StringUtils.isNotEmpty(userInputWhereParam.getBeginParamName()) && StringUtils.isNotEmpty(userInputWhereParam.getEndParamName()), "whereParamNodeUseCompareType is FOREACH  BETWEEN beginParamName/endParamName Can't null")
                    ;
                }
                if (whereParamNodeUseCompareType.equals(WhereParamNodeUseCompareType.FOREACH) && !Collection.class.isAssignableFrom(paramType)) {
                    log.error("whereParamNodeUseCompareType is FOREACH ,paramType need Collection !");
                    new IllegalArgumentException("whereParamNodeUseCompareType is FOREACH ,paramType need Collection !");
                }
            }
        }
    }

    private TableInfo getVirtualTableInfo(String tableName) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        String camelCaseTableName = StrUtil.toCamelCase(tableName);
        //首字母大写
        tableInfo.setCapitalizeTableName(StringUtils.capitalize(camelCaseTableName));
        return tableInfo;
    }
}
