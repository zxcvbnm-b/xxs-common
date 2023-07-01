package xxs.common.module.codegenerate.filter;

import com.google.auto.service.AutoService;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.template.ParamDTOTemplate;

/**
 * ParamDTO拦截器
 * @author xxs
 */
@AutoService(IGenerateFilter.class)
public class ParamDTOGenerateFilter implements IGenerateFilter {
    @Override
    public void init(CodeGenerateContext generateContext) {
        generateContext.addTemplate(new ParamDTOTemplate());
    }
}
