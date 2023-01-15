package xxs.common.module.codegenerate.filter;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.Template;

/**/
public interface IGenerateFilter {
    //在遍历模板执行之前
    default void init(CodeGenerateContext generateContext) {
    }

    ;

    //执行表时
    default void tableExePre(CodeGenerateContext generateContext, TableInfo tableInfo) {
    }

    ;

    //在执行某一个模板之前
    default void templateExePre(CodeGenerateContext generateContext, TableInfo tableInfo, Template template) {
    }

    ;
}
