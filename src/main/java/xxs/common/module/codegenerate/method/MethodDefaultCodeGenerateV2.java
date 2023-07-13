package xxs.common.module.codegenerate.method;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.util.JdbcConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import xxs.common.module.codegenerate.*;
import xxs.common.module.codegenerate.cache.TableInfoTemCache;
import xxs.common.module.codegenerate.method.enums.MethodReturnType;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.enums.WhereParamOperationType;
import xxs.common.module.codegenerate.method.model.*;
import xxs.common.module.codegenerate.method.sql.MybatisSqlWhereDisposeUtils;
import xxs.common.module.codegenerate.method.sql.SqlWhereExpressionItemParseUtils;
import xxs.common.module.codegenerate.method.template.MethodAllTemplate;
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
 * 方法级别代码生成--多例的 每次生成都生成一次
 * 用法：sql:select * from actor where actor_id in ('#{actorId}') 注意 ('#{actorId}')应该是贴紧的 ，否则会替换失败，因为sql在AST时会被格式化处理
 *
 * @author xxs
 */
@Slf4j
public class MethodDefaultCodeGenerateV2 {
    private VelocityTemplateEngine velocityTemplateEngine = VelocityTemplateEngine.getVelocityTemplateEngineInstance();
    private MybatisSqlWhereDisposeUtils mybatisSqlWhereDisposeUtils = new MybatisSqlWhereDisposeUtils();
    private TableService tableService;
    private MethodCodeGenerateContext codeGenerateContext;
    private SqlWhereExpressionItemParseUtils sqlWhereExpressionItemParseUtils;

    public MethodDefaultCodeGenerateV2(TableService tableService) {
        this.tableService = tableService;
        this.codeGenerateContext = new MethodCodeGenerateContext().initMethodCodeGenerateContext();
        this.sqlWhereExpressionItemParseUtils = new SqlWhereExpressionItemParseUtils(new TableInfoTemCache(tableService));
    }

    public MethodDefaultCodeGenerateV2(TableService tableService, MethodCodeGenerateContext codeGenerateContext) {
        this.tableService = tableService;
        this.codeGenerateContext = codeGenerateContext;
        this.sqlWhereExpressionItemParseUtils = new SqlWhereExpressionItemParseUtils(new TableInfoTemCache(tableService));
    }

    public static void main(String[] args) throws Exception {
        MethodDefaultCodeGenerateV2 methodDefaultCodeGenerate = new MethodDefaultCodeGenerateV2(new DBTableServiceImpl());
        methodDefaultCodeGenerate.singleTableCodeGenerator("actor", "getCctor", "select * from film a inner join film_actor b on a.film_id = b.film_id inner join film_category c on a.film_id = c.film_id where a.film_id in ('#{filmIds}') and a.film_id = ('#{filmId}') and a.description like '#{description}' and a.last_update between '#{beginLastUpdate}' and '#{endLastUpdate}'", ParamType.QUERY_PARAM);
    }

    /**
     * @param tableName  表名，用于匹配已经存在的文件
     * @param searchName 查询名字
     * @param sql        需要处理的sql
     * @param sql        需要处理的sql
     * @throws Exception
     */
    public void singleTableCodeGenerator(String tableName, String searchName, String sql, ParamType paramType) throws Exception {
        //输入参数检查
        this.inputValueValid(tableName, sql, paramType);
        MethodGenParamContext methodGenParamContext = new MethodGenParamContext();
        methodGenParamContext.setSearchName(searchName);
        //驼峰后 首字母大写
        methodGenParamContext.setCapitalizeSearchName(StringUtils.capitalize(StrUtil.toCamelCase(searchName)));
        //获取虚拟table， 用于匹配到当前代码生成环境的文件的名称（比如用于追加方法到类中 如果存在的话）
        TableInfo tableInfo = this.getVirtualTableInfo(tableName);
        //根据sql获取sql的列（只是返回投影的列，可能在数据库中，这个列根本就不存在）的信息，ResultDTO的生成需要用到投影列，但是参数的生成需要用到表的列的
        List<SearchColumnInfo> searchColumnInfoBySearchSql = tableService.getSearchColumnInfoBySearchSql(sql);
        if (CollectionUtils.isEmpty(searchColumnInfoBySearchSql)) {
            log.warn("singleTableCodeGenerator not return any column info !");
            return;
        }
        //重新包装投影列,根据searchColumnInfoBySearchSql返回的值
        String realSql = this.wrapSqlProjection(sql, searchColumnInfoBySearchSql);
        //转换sql为mybatis格式
        String realMybatisSql = this.handleSql(realSql, methodGenParamContext, paramType);
        methodGenParamContext.setRealSql(realMybatisSql);
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
            if (codeGenerateContext.isOnlyGenerateMethodAllTemplate()) {
                if (template instanceof MethodAllTemplate) {
                    velocityTemplateEngine.generate(stringObjectMap, template.getTemplateFilePathName(), this.wrapFileName(template.getOutFilePathName(codeGenerateContext, tableInfo), template, methodGenParamContext.getCapitalizeSearchName()), template.append(), false);
                }
            } else {
                velocityTemplateEngine.generate(stringObjectMap, template.getTemplateFilePathName(), this.wrapFileName(template.getOutFilePathName(codeGenerateContext, tableInfo), template, methodGenParamContext.getCapitalizeSearchName()), template.append(), false);
            }
        }
    }

    /**
     * 处理sql语句 转换为mybatis识别的语法，并设置 whereParamList
     *
     * @param sql
     * @param methodGenParamContext
     * @return
     */
    private String handleSql(String sql, MethodGenParamContext methodGenParamContext, ParamType paramType) throws Exception {
        List<SqlWhereExpressionOperateParam> sqlWhereExpressionOperateParams = mybatisSqlWhereDisposeUtils.processSelectBody(sql);
        sqlWhereExpressionItemParseUtils.initSqlWhereExpressionOperateParamColumnInfo(sqlWhereExpressionOperateParams);
        List<WhereParam> whereParamList = new ArrayList<>();
        for (SqlWhereExpressionOperateParam sqlWhereExpressionOperateParam : sqlWhereExpressionOperateParams) {
            WhereParam whereParam = new WhereParam();
            whereParam.setBeginParamName(sqlWhereExpressionOperateParam.getBeginParamName());
            whereParam.setEndParamName(sqlWhereExpressionOperateParam.getEndParamName());
            whereParam.setParamType(sqlWhereExpressionOperateParam.getColumnJavaType());
            whereParam.setWhereParamOperationType(sqlWhereExpressionOperateParam.getSqlWhereParamType());
            whereParam.setColumnName(sqlWhereExpressionOperateParam.getColumnName());
            whereParam.setParamName(sqlWhereExpressionOperateParam.getWhereParamName());
            whereParam.setLogicOperator(sqlWhereExpressionOperateParam.getLogicOperator());
            XMLWhereParamNode xmlWhereParamNode = XmlWhereParamNodeFactory.create(whereParam, paramType);
            whereParamList.add(whereParam);
            System.out.println(sqlWhereExpressionOperateParam.getFindPattern());
            System.out.println(xmlWhereParamNode.getWhereParamNode());
            String replacementTemplate = null;
            String replacementTemplatePre = "\n";
            if (sqlWhereExpressionOperateParam.getLogicOperator() == null) {
                replacementTemplatePre = "1=1 \n";
            }
            replacementTemplate = replacementTemplatePre + xmlWhereParamNode.getWhereParamNode();
            sql = ReUtil.replaceAll(sql, sqlWhereExpressionOperateParam.getFindPattern(), replacementTemplate);
        }
        methodGenParamContext.setWhereParamList(whereParamList);
        return sql;
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
        //设置方法参数
        this.setMethodParamInfo(methodGenParamContext, methodGenVelocityParam);
        //设置xml的内容
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
        String xmlSqlContent = realSql;
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
        String mapperMethodParams = "";
        if (methodGenParamContext.getParamType().equals(ParamType.DTO)) {
            methodParamNames = methodGenParamContext.getSearchName() + "Param";
            methodParams = methodGenParamContext.getCapitalizeSearchName() + "Param " + methodParamNames;
            methodGenVelocityParam.setParamDTOType(true);
            mapperMethodParams = String.format("@Param(\"condition\") %s", methodParamNames);
        } else if (methodGenParamContext.getParamType().equals(ParamType.QUERY_PARAM)) {
            List<WhereParam> whereParamList = methodGenParamContext.getWhereParamList();
            StringJoiner methodParamStringJoiner = new StringJoiner(", ");
            StringJoiner mapperMethodParamStringJoiner = new StringJoiner(", ");
            StringJoiner methodParamNameStringJoiner = new StringJoiner(", ");
            for (WhereParam whereParam : whereParamList) {
                if (!WhereParamOperationType.BETWEEN.equals(whereParam.getWhereParamOperationType())) {
                    String paramName = whereParam.getParamName();
                    String camelCaseParamName = StrUtil.toCamelCase(paramName);
                    methodParamNameStringJoiner.add(camelCaseParamName);
                    String methodParamString = whereParam.getParamType().getSimpleName() + " " + camelCaseParamName;
                    if (WhereParamOperationType.IN.equals(whereParam.getWhereParamOperationType())) {
                        methodParamString = String.format("List<%s>", whereParam.getParamType().getSimpleName()) + " " + camelCaseParamName;
                    }
                    methodParamStringJoiner.add(methodParamString);
                    mapperMethodParamStringJoiner.add(String.format("@Param(\"%s\")", camelCaseParamName) + " " + methodParamString);
                } else {
                    //添加开始参数
                    String beginParamName = whereParam.getBeginParamName();
                    String camelCaseBeginParamName = StrUtil.toCamelCase(beginParamName);
                    methodParamNameStringJoiner.add(camelCaseBeginParamName);
                    String beginParamNameMethodParamString = whereParam.getParamType().getSimpleName() + " " + camelCaseBeginParamName;
                    methodParamStringJoiner.add(beginParamNameMethodParamString);
                    mapperMethodParamStringJoiner.add(String.format("@Param(\"%s\")", camelCaseBeginParamName) + " " + beginParamNameMethodParamString);

                    //添加结束参数
                    String endParamName = whereParam.getEndParamName();
                    String camelCaseEndParamName = StrUtil.toCamelCase(endParamName);
                    methodParamNameStringJoiner.add(camelCaseEndParamName);
                    String endParamNameMethodParamString = whereParam.getParamType().getSimpleName() + " " + camelCaseEndParamName;
                    methodParamStringJoiner.add(endParamNameMethodParamString);
                    mapperMethodParamStringJoiner.add(String.format("@Param(\"%s\")", camelCaseEndParamName) + " " + endParamNameMethodParamString);
                }
            }
            methodParamNames = methodParamNameStringJoiner.toString();
            methodParams = methodParamStringJoiner.toString();
            mapperMethodParams = mapperMethodParamStringJoiner.toString();
        }
        methodGenVelocityParam.setMethodParams(methodParams);
        methodGenVelocityParam.setMethodParamNames(methodParamNames);
        methodGenVelocityParam.setMapperMethodParams(mapperMethodParams);
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

    /**
     * 输入参数验证
     *
     * @param tableName
     * @param sql
     */
    private void inputValueValid(String tableName, String sql, ParamType paramType) {
        if (StringUtils.isEmpty(sql)) {
            log.error("sql Can't null  null  !");
            new IllegalArgumentException("sql Can't null  !");
        }
        if (paramType == null) {
            log.error("paramType Can't null  null  !");
            new IllegalArgumentException("paramType Can't null  !");
        }
        //判断sql是不是一个查询sql
        Matcher matcher = Constants.SELECT_SQL_PATTERN.matcher(sql);
        Assert.isTrue(!matcher.find(), "校验SQL只能是SELECT类型");
        Assert.isTrue(sql.split(Constants.SQL_PARTITION).length == 1, "只支持一条SQL语句");
        Assert.isTrue(DruidSqlDisposeUtils.validSql(sql, JdbcConstants.MYSQL.name()), "druid 校验sql出错！");
        Assert.isTrue(StringUtils.isNotEmpty(tableName), "tableName Can't null  !");
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
