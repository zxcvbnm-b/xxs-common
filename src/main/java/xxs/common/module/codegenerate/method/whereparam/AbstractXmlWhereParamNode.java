package xxs.common.module.codegenerate.method.whereparam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PropertyPlaceholderHelper;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.method.model.WhereParam;

/**
 * @author xxs
 */
@Slf4j
public abstract class AbstractXmlWhereParamNode implements XMLWhereParamNode {
    protected String wherePre = "";
    protected WhereParam whereParam;
    protected ParamType paramType;
    public static final String PARAM_NAME_PLACEHOLDER_HELPER = "paramName";
    public static final String COLUMN_NAME_PLACEHOLDER_HELPER = "columnName";
    public static final String COMPARE_SYMBOL_PLACEHOLDER_HELPER = "compareSymbol";
    public final static PropertyPlaceholderHelper PLACEHOLDER_HELPER = new PropertyPlaceholderHelper("${", "}");
    public AbstractXmlWhereParamNode(WhereParam whereParam, ParamType paramType) {
        this.whereParam = whereParam;
        this.paramType = paramType;
        if (ParamType.DTO.equals(paramType)) {
            wherePre = "condition";
        } else if (ParamType.QUERY_PARAM.equals(paramType)) {
            wherePre = "";
        }
    }

}
