package xxs.common.module.codegenerate.filter;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.Template;

/**
 * 代码生成拦截接口
 */
public interface IGenerateFilter {
    /**
     * 在遍历模板执行之前
     */

    default void init(CodeGenerateContext generateContext) {
    }

    /**
     * 表逻辑执行之前--可以修改表的信息，还有一对一 一对多的关联关系，可以实现比较复杂的内容
     */
    default void tableExePre(CodeGenerateContext generateContext, TableInfo tableInfo) {
    }

    /**
     * 在执行某一个模板之前
     */
    default void templateExePre(CodeGenerateContext generateContext, TableInfo tableInfo, Template template) {
    }
}
