package xxs.common.module.codegenerate.model;

import lombok.Data;

/**
 * @author xiongsongxu
 */
@Data
public class RelationTableInfo {
    private String relationTableName;
    private String relationColumnName;
    /**
     * 关联的唯一的名 用于在删除的时候可以匹配删除，用在更新字段的时候做判断是否已经存在*
     */
    private String relationUniqueColumnName;
    private boolean one2One;
}
