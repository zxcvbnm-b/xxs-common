package xxs.common.module.datagenerate.db.dto;

import lombok.Data;
import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;

import java.util.List;

/**
 * 列信息
 *
 * @author issuser
 */
@Data
public class TableColumnInfo {
    /**
     * 列的名称
     */
    private String columnName;
    /**
     * 是否是主键
     */
    private boolean isPrimaryKey;
    /**
     * 这列的详细信息
     */
    private String columnInfo;
    /**
     * 是否自增
     */
    private boolean isAutoincrement;
    /**
     * 列的约束信息
     */
    private List<ColumnConstraint> columnConstraints;
    /**
     * 类型信息
     */
    private JdbcTypeInfo jdbcTypeInfo;
    /**
     * 备注
     */
    private String comment;
}
