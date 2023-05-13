package xxs.common.module.codegenerate.method.model;

import lombok.Data;
import xxs.common.module.codegenerate.model.SearchColumnInfo;

import java.util.List;

/**
 * 方法生成模板参数信息
 *
 * @author xxs
 */
@Data
public class MethodGenVelocityParam {
    /**
     * 查询方法名称
     */
    private String searchMethodName;

    /**
     * searchMethodName首字母大写
     */
    private String capitalizeSearchMethodName;

    /**
     * 方法参数列表，比如 int a,int b
     */
    private String methodParams;

    /**
     * 方法参数列表，比如 a,b
     */
    private String methodParamNames;

    /**
     * 方法参数列表，比如 @Param("a")int a,@Param("a")int b
     */
    private String mapperMethodParams;

    /**
     * 方法返回参数全类名
     */
    private String returnTypeName;
    /**
     * 方法返回参数简称
     */
    private String returnTypeSimpleName;

    /**
     * 查询方法的xml的内容
     */
    private String xmlContent;

    /**
     * where参数信息 比如参数类型，参数列名等 参数查询模式 ，比如是等值查询啊， 还是时间范围查询啊，还是范围查询啊，还是for循环类型啊 like啊 用于返回dto和xml
     */
    private List<WhereParam> whereParamList;

    /**
     * 所有的查询返回列 用于返回dto
     */
    private List<SearchColumnInfo> returnSelectList;

    /**
     *
     */
    private boolean paramDTOType;
}
