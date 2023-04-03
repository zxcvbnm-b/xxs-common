package xxs.common.module.codegenerate.filter;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.template.ParamDTOTemplate;
import xxs.common.module.codegenerate.template.ResultDTOTemplate;

import javax.xml.transform.Result;

/**
 * ResultDTO拦截器
 *
 * @author xxs
 */
public class ResultDTOGenerateFilter implements IGenerateFilter {
    @Override
    public void init(CodeGenerateContext generateContext) {
        generateContext.addTemplate(new ResultDTOTemplate());
    }
}
