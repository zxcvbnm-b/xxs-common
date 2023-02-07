package xxs.common.module.codegenerate.filter;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.Template;

import java.util.ArrayList;
import java.util.List;
/*拦截器--用于扩展功能，比如paramDTO*/
public class GenerateFilterContext implements IGenerateFilter {
    private List<IGenerateFilter> generateFilters = new ArrayList<>();

    {
        generateFilters.add(new ParamDTOGenerateFilter());
        generateFilters.add(new ResultDTOGenerateFilter());
        generateFilters.add(new StoredProcedureGenerateFilter());
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

    public void putGenerateFilter(IGenerateFilter iGenerateFilter) {
        generateFilters.add(iGenerateFilter);
    }
}
