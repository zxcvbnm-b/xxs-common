package xxs.common.module.codegenerate.filter;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.ParamDTOTemplate;
import xxs.common.module.codegenerate.template.Template;

/**
 * ParamDTO拦截器
 * @author xxs
 */
public class ParamDTOGenerateFilter implements IGenerateFilter {
    @Override
    public void init(CodeGenerateContext generateContext) {
        generateContext.addTemplate(new ParamDTOTemplate(generateContext));
    }
}
