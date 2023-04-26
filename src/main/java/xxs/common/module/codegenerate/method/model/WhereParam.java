package xxs.common.module.codegenerate.method.model;

import lombok.Data;
import xxs.common.module.codegenerate.method.enums.WhereSearchParamType;
import xxs.common.module.codegenerate.model.SearchColumnInfo;

/**
 * @author xxs
 */
@Data
public class WhereParam {
    /**
     * 列名
     */
    private String columnName;
    /**
     * where 查询参数类型
     */
    private WhereSearchParamType whereSearchParamType;

    /**
     * 查询列信息
     */
    private SearchColumnInfo searchColumnInfo;
}
