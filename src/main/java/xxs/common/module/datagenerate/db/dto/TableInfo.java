package xxs.common.module.datagenerate.db.dto;

import java.util.List;
import java.util.Set;

/**
 * 表信息
 *
 * @author xxs
 */
public class TableInfo {
    /**
     * 所有列信息
     */
    private List<TableColumnInfo> tableColumnInfos;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 主键列
     */
    private Set<String> primaryKeyColumns;

    public List<TableColumnInfo> getTableColumnInfos() {
        return tableColumnInfos;
    }

    public void setTableColumnInfos(List<TableColumnInfo> tableColumnInfos) {
        this.tableColumnInfos = tableColumnInfos;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Set<String> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public void setPrimaryKeyColumns(Set<String> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }
}
