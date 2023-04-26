package xxs.common.module.codegenerate.method.model;

import lombok.Data;
import xxs.common.module.codegenerate.method.enums.MethodReturnType;
import xxs.common.module.codegenerate.method.enums.ParamType;

import java.util.List;

/**
 * 方法生成参数信息
 *
 * @author xxs
 */
@Data
public class MethodGenParam {
    /**
     * 真正的sql
     */
    private String realSql;
    /**
     * 表名 为经过处理的表名
     */
    private String tableName;
    /**
     * 驼峰首字母大写TableName
     */
    private String capitalizeTableName;
    /**
     * where参数信息 比如参数类型，参数列名等 参数查询模式 ，比如是等值查询啊， 还是时间范围查询啊，还是范围查询啊，还是for循环类型啊 like啊
     */
    private List<WhereParam> whereParamList;

    /**
     * where参数模式，是使用dto包装，还是使用那种简单不包装的方式
     */
    private ParamType paramType = ParamType.DTO;

    /**
     * 返回值类型，比如list，分页，int Integer，dto等等等类型。
     */
    private MethodReturnType returnType;
}
