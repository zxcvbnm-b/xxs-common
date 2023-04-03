package xxs.common.module.codegenerate.mybatisplus;

import xxs.common.module.codegenerate.model.RelationTableInfo;

import java.util.List;

/**
 * @author xxs
 */
public interface CodeGenerator {
    /**
     * 多主表生成（无一对一 一对多关系）
     * @param tables 表名集合，使用 ，隔开
     * @throws Exception
     */
    void singleTableCodeGenerator(String tables) throws Exception;

    /**
     * 单主表生成（有一对一 一对多关系）
     * @param mainTableName
     * @param relationTableInfos
     * @throws Exception
     */
    void relationCodeGenerator(String mainTableName, List<RelationTableInfo> relationTableInfos) throws Exception;
}
