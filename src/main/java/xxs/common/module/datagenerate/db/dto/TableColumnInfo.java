package xxs.common.module.datagenerate.db.dto;

import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;

import java.util.List;

/**
 * 列信息
 *
 * @author issuser
 */
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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public String getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(String columnInfo) {
        this.columnInfo = columnInfo;
    }

    public boolean isAutoincrement() {
        return isAutoincrement;
    }

    public void setAutoincrement(boolean autoincrement) {
        isAutoincrement = autoincrement;
    }

    public List<ColumnConstraint> getColumnConstraints() {
        return columnConstraints;
    }

    public void setColumnConstraints(List<ColumnConstraint> columnConstraints) {
        this.columnConstraints = columnConstraints;
    }

    public JdbcTypeInfo getJdbcTypeInfo() {
        return jdbcTypeInfo;
    }

    public void setJdbcTypeInfo(JdbcTypeInfo jdbcTypeInfo) {
        this.jdbcTypeInfo = jdbcTypeInfo;
    }
}
