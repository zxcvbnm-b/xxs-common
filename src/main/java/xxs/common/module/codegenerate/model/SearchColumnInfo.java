package xxs.common.module.codegenerate.model;

import lombok.Data;
import xxs.common.module.codegenerate.TypeMapperRegistry;

/**
 * 执行sql后列基本信息
 *
 * @author xxs
 */
@Data
public class SearchColumnInfo {
    /**
     * 列名（可能会被重写）
     */
    private String columnName;
    /**
     * 真正列名
     */
    private String realColumnName;
    /**
     * 在获取时就进行小驼峰
     */
    private String camelCaseColumnName;
    /**
     * 首字母大写
     */
    private String capitalizeColumnName;
    /**
     * jdbc类型名称
     */
    private String jdbcTypeName;
    /**
     * java类型
     */
    private Class javaType;
    /**
     * jdbc类型code
     */
    private int jdbcTypeCode;
    /**
     * 备注
     */
    private String comment;
    /**
     * 列名是否被重写过了，当sql是连接查询时，列名会被重写
     */
    private boolean columnNameRewrite;
    /**
     * 表名
     */
    private String tableName;

    public Class getJavaType() {
        return TypeMapperRegistry.getJavaType(jdbcTypeCode);
    }
}
