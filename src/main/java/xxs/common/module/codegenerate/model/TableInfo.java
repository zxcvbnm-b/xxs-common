package xxs.common.module.codegenerate.model;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 表的基础信息和关联关系维护DTO
 *
 * @author issuser
 */
@Data
public class TableInfo {
    /**
     * 表名
     */
    private String name;
    /**
     * 表的备注
     */
    private String comment;
    /**
     * 表的类型？
     */
    private String tableType;
    /**
     * 驼峰 tableName
     */
    private String tableName;
    /**
     * 驼峰首字母大写TableName
     */
    private String capitalizeTableName;
    /**
     * 列信息--包含主键
     */
    private List<ColumnInfo> columnInfos;
    /**
     * 主键列
     */
    private ColumnInfo keyColumnInfo;

    /**
     * 一对多的关系 要处理级联操作
     */
    private List<TableRelationship> tableRelationships;

    /**
     * 获取列的java类型的全路径名
     */
    public Set<String> getColumnTypePackageNames() {
        Set<String> result = new HashSet<>();
        if (CollectionUtils.isEmpty(columnInfos)) {
            return result;
        }
        for (ColumnInfo columnInfo : columnInfos) {
            result.add(columnInfo.getJavaType().getName());
        }
        return result;
    }
}
