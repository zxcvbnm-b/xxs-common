package xxs.common.module.codegenerate.method.model;

import lombok.Data;

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
     * 是否是where参数
     */
    private boolean forEachWhereParam;
}
