package xxs.common.module.codegenerate.method;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import xxs.common.module.codegenerate.*;
import xxs.common.module.codegenerate.config.DataSourceConfig;
import xxs.common.module.codegenerate.model.SearchColumnInfo;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.Template;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 方法级别默认代码生成
 *
 * @author xxs
 */
public class MethodDefaultCodeGenerate {
    private VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
    private LoadTableService loadTableService = new LoadTableService(new DataSourceConfig());
    private CodeGenerateContext codeGenerateContext = new MethodCodeGenerateContext().initMethodCodeGenerateContext();

    public static void main(String[] args) {
        MethodDefaultCodeGenerate methodDefaultCodeGenerate = new MethodDefaultCodeGenerate();
    }

    /**
     *
     * @param tableName 表名，用于匹配已经存在的文件
     * @param sql 需要处理的sql
     * @param whereParamSet 作为参数的where列
     * @throws Exception
     */
    public void singleTableCodeGenerator(String tableName, String sql, Set<String> whereParamSet) throws Exception {
        List<SearchColumnInfo> searchColumnInfoBySearchSql = loadTableService.getSearchColumnInfoBySearchSql("sql");
        //where 条件列
        Set<SearchColumnInfo> whereColumns = new HashSet<>();
        for (SearchColumnInfo searchColumnInfo : searchColumnInfoBySearchSql) {
            if (whereParamSet.contains(searchColumnInfo.getRealColumnName())) {
                whereColumns.add(searchColumnInfo);
            }
        }
        VelocityParamBuilder velocityParamBuilder = codeGenerateContext.getVelocityParamBuilder();
        Map<String, Object> stringObjectMap = velocityParamBuilder.get();
        stringObjectMap.put("whereColumns", whereColumns);
        stringObjectMap.put("allColumns", searchColumnInfoBySearchSql);
        Set<Template> genTemplate = codeGenerateContext.getTemplates();
        for (Template template : genTemplate) {
            TableInfo tableInfo = getVirtualTableInfo(tableName);
            velocityTemplateEngine.generate(stringObjectMap, template.getTemplateFilePathName(), template.getOutFilePathName(codeGenerateContext, tableInfo), template.append(), false);
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
