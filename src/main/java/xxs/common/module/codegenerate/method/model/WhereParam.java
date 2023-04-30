package xxs.common.module.codegenerate.method.model;

import lombok.Data;
import xxs.common.module.codegenerate.model.SearchColumnInfo;

/**
 * @author xxs
 */
@Data
public class WhereParam extends UserInputWhereParam{
    /**
     * 查询列信息
     */
    private SearchColumnInfo searchColumnInfo;
}
