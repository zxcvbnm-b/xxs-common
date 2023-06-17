package xxs.common.module.codegenerate.method.model;

import lombok.Data;
import xxs.common.module.codegenerate.method.enums.LogicOperator;
import xxs.common.module.codegenerate.method.enums.WhereParamOperationType;
;
/**
 * @author xxs
 */
@Data
public class UserInputWhereParam {
    /**
     * 列名（对应数据库列） 必填
     */
    private String columnName;

    /**
     * 参数名（对应方法入参）
     */
    private String paramName;

    /**
     * where 查询参数类型 必填
     */
    private Class<?> paramType;


    /**
     * where 查询参数类型 必填
     */
    private WhereParamOperationType whereParamOperationType = WhereParamOperationType.EQ;

    /**
     * 如果当前参数是一个between的范围查询，那么需要beginParamName
     */
    private String beginParamName;

    /**
     * 如果当前参数是一个between的范围查询，那么需要endParamName
     */
    private String endParamName;

    /**
     * and or or
     */
    private LogicOperator logicOperator;


}
