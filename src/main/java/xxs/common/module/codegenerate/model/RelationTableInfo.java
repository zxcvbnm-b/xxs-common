package xxs.common.module.codegenerate.model;

import lombok.Data;

/**
 * @author xiongsongxu
 */
@Data
public class RelationTableInfo {
    private String relationTableName;
    private String relationColumnName;
    private boolean one2One;
}
