package xxs.common.module.codegenerate.filter;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 拦截器--用于扩展功能，比如paramDTO
 *
 * @author xxs
 */
public class GenerateFilterContext implements IGenerateFilter {
    private List<IGenerateFilter> generateFilters = new ArrayList<>();

    {
        ServiceLoader.load(IGenerateFilter.class).forEach(iGenerateFilter -> {
            generateFilters.add(iGenerateFilter);
        });

    }

    @Override
    public void init(CodeGenerateContext generateContext) {
        for (IGenerateFilter generateFilter : generateFilters) {
            generateFilter.init(generateContext);
        }
    }

    @Override
    public void tableExePre(CodeGenerateContext generateContext, TableInfo tableInfo) {
        for (IGenerateFilter generateFilter : generateFilters) {
            generateFilter.tableExePre(generateContext, tableInfo);
        }
    }

    @Override
    public void templateExePre(CodeGenerateContext generateContext, TableInfo tableInfo, Template template) {
        for (IGenerateFilter generateFilter : generateFilters) {
            generateFilter.templateExePre(generateContext, tableInfo, template);
        }
    }

    public void addGenerateFilter(IGenerateFilter iGenerateFilter) {
        generateFilters.add(iGenerateFilter);
    }

}
