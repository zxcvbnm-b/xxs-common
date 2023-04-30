package xxs.common.module.codegenerate.method.model;

import lombok.Data;
import xxs.common.module.codegenerate.method.enums.MethodReturnType;
import xxs.common.module.codegenerate.method.enums.ParamType;
import xxs.common.module.codegenerate.model.SearchColumnInfo;

import java.util.List;

/**
 * 方法生成参数信息
 *
 * @author xxs
 */
@Data
public class MethodGenParamContext {
    /**
     * 真正的sql
     */
    private String realSql;

    /**
     * 查询的名字 用于方法名称 dto类型的参数名称
     */
    private String searchName;
    /**
     * 查询的名字首字母大写 用于DTO名字生成 （result dto，param dto）
     */
    private String capitalizeSearchName;

    /**
     * where参数信息 比如参数类型，参数列名等 参数查询模式 ，比如是等值查询啊， 还是时间范围查询啊，还是范围查询啊，还是for循环类型啊 like啊 用于返回dto和xml
     */
    private List<WhereParam> whereParamList;

    /**
     *  xmlWhereParamNodeList 根据 whereParamList生成得到
     */
    private List<String> xmlWhereParamNodeList;

    /**
     * 所有的查询返回列 用于返回dto
     */
    private List<SearchColumnInfo> allReturnSelectList;

    /**
     * where参数模式，是使用dto包装，还是使用那种简单不包装的方式
     */
    private ParamType paramType = ParamType.DTO;

    /**
     * 返回值类型，比如list，int Integer，dto等等等类型。
     */
    private MethodReturnType returnType = MethodReturnType.LIST_DTO;

    /**
     * 返回值类型class 如果没有设置，那么就是LIST_DTO 对于LIST_DTO和DTO不需要设置值
     */
    private Class<?> returnTypeClass;
}
