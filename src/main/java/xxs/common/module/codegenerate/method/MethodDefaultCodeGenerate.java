package xxs.common.module.codegenerate.method;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 方法级别默认代码生成
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
        userInputWhereParam.setSearchParamType(Integer.class);
        userInputWhereParam.setColumnName("ID");
        userInputWhereParam.setParamName("id");
        userInputWhereParam.setWhereParamNodeUseCompareType(WhereParamNodeUseCompareType.EQ);
        params.add(userInputWhereParam);
        methodDefaultCodeGenerate.singleTableCodeGenerator("city", "getCity", "select * from city", params);
    }

    /**
     * @param tableName     表名，用于匹配已经存在的文件
     * @param searchName    查询名字
     * @param sql           需要处理的sql
     * @param whereParamSet 作为参数的where列
     * @throws Exception
     */
    public void singleTableCodeGenerator(String tableName, String searchName, String sql, Set<UserInputWhereParam> whereParamSet) throws Exception {
        this.inputValueValid(tableName, sql);
        MethodGenParamContext methodGenParamContext = new MethodGenParamContext();
        //TODO 可能需要对sql进行处理 比如列名重复，那么可能需要处理下 select 投影， 重新修改sql语句的返回列根据 searchColumnInfoBySearchSql
        methodGenParamContext.setRealSql(sql);
        methodGenParamContext.setSearchName(searchName);
        methodGenParamContext.setCapitalizeSearchName(StringUtils.capitalize(StrUtil.toCamelCase(searchName)));
        TableInfo tableInfo = this.getVirtualTableInfo(tableName);
        List<SearchColumnInfo> searchColumnInfoBySearchSql = loadTableService.getSearchColumnInfoBySearchSql(sql);
        if (CollectionUtils.isEmpty(searchColumnInfoBySearchSql)) {
            log.warn("singleTableCodeGenerator not return any column info !");
            return;
        }
        List<WhereParam> whereParamList = this.getWhereParams(whereParamSet, searchColumnInfoBySearchSql);
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

    private String wrapFileName(String outFilePathName, Template template, String capitalizeSearchName) {
        if (!(template instanceof MethodParamDTOTemplate || template instanceof MethodResultDTOTemplate)) {
            return outFilePathName;
        }
        String result = outFilePathName;
        String folder = outFilePathName.substring(0, outFilePathName.lastIndexOf("\\"));
        String fileSuffix = outFilePathName.substring(outFilePathName.lastIndexOf("."));
        if (template instanceof MethodParamDTOTemplate) {
            result = folder + File.separator + capitalizeSearchName + "Param" + fileSuffix;
        }
        if (template instanceof MethodResultDTOTemplate) {
            result = folder + File.separator + capitalizeSearchName + "Result" + fileSuffix;
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

    private List<WhereParam> getWhereParams(Set<UserInputWhereParam> whereParamSet, List<SearchColumnInfo> searchColumnInfoBySearchSql) {
        List<WhereParam> whereParamList = new ArrayList<>();
        //where 条件列处理
        Set<String> sqlSearchColumnNameSet = new HashSet<>();
        Map<String, List<SearchColumnInfo>> stringSearchColumnInfoMap = new HashMap<>(20);
        if (!CollectionUtils.isEmpty(whereParamSet)) {
            sqlSearchColumnNameSet = searchColumnInfoBySearchSql.stream().map(SearchColumnInfo::getRealColumnName).collect(Collectors.toSet());
            stringSearchColumnInfoMap = searchColumnInfoBySearchSql.stream().collect(Collectors.groupingBy(SearchColumnInfo::getRealColumnName));
        }
        for (UserInputWhereParam userInputWhereParam : whereParamSet) {
            WhereParam whereParam = new WhereParam();
            BeanUtil.copyProperties(userInputWhereParam, whereParam);
            if (sqlSearchColumnNameSet.contains(userInputWhereParam.getColumnName())) {
                SearchColumnInfo searchColumnInfo = stringSearchColumnInfoMap.get(userInputWhereParam.getColumnName()).get(0);
                whereParam.setSearchColumnInfo(searchColumnInfo);
                //TODO 现在只能选到存在的字段
                whereParamList.add(whereParam);
            }
        }
        return whereParamList;
    }

    private void inputValueValid(String tableName, String sql) {
        if (StringUtils.isEmpty(sql)) {
            log.error("sql is not null  !");
            new IllegalArgumentException("sql is not null  !");
        }
        if (StringUtils.isEmpty(tableName)) {
            log.error("tableName is not null  !");
            new IllegalArgumentException("tableName is not null  !");
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
