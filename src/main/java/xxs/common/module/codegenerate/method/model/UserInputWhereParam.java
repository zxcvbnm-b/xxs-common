package xxs.common.module.codegenerate.method.model;

import lombok.Data;
import xxs.common.module.codegenerate.method.enums.WhereParamNodeUseCompareType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xxs
 */
@Data
public class UserInputWhereParam {
    /**
     * 列名（对应数据库列）
     */
    private String columnName;

    /**
     * 参数名（对应方法入参）
     */
    private String paramName;

    /**
     * where 查询参数类型
     */
    private Class<?> searchParamType;


    /**
     * where 查询参数类型
     */
    private WhereParamNodeUseCompareType whereParamNodeUseCompareType = WhereParamNodeUseCompareType.EQ;

    /**
     * 当前查询参数的其他属性 如果列类型是between类型，那么需要传入：
     */
    private Map<String, Object> attribute = new HashMap<>();
}
