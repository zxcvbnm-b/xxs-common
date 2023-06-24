package xxs.common.module.datagenerate.db.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 表信息
 *
 * @author xxs
 */
@Data
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
     * 表的备注
     */
    private String comment;
    /**
     * 主键列
     */
    private Set<String> primaryKeyColumns;
}
