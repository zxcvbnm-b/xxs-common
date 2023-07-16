package xxs.common.module.codegenerate.method.whereparam;

import com.alibaba.druid.DbType;
import lombok.extern.slf4j.Slf4j;
import xxs.common.module.codegenerate.method.enums.LogicOperator;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
@Slf4j
public abstract class AbstractXmlWhereParamNode implements XMLWhereParamNode {
    protected DbType dbType;
    protected String wherePre = "";
    protected WhereParam whereParam;
    protected ParamType paramType;
    public static final String PARAM_NAME_PLACEHOLDER_HELPER = "paramName";
    public static final String COLUMN_NAME_PLACEHOLDER_HELPER = "columnName";
    public static final String COMPARE_SYMBOL_PLACEHOLDER_HELPER = "compareSymbol";
    public static final String LOGIC_OPERATOR_PLACEHOLDER_HELPER = "logicOperator";

    public AbstractXmlWhereParamNode(DbType dbType, WhereParam whereParam, ParamType paramType) {
        this.dbType = dbType;
        this.whereParam = whereParam;
        this.paramType = paramType;
        if (ParamType.DTO.equals(paramType)) {
            wherePre = "condition.";
        }
        if (whereParam.getLogicOperator() == null) {
            whereParam.setLogicOperator(LogicOperator.AND);
        }
        if (dbType == null) {
            dbType = DbType.mysql;
        }
    }

}
