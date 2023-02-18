package xxs.common.module.codegenerate.model;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表的关联关系 ，通过某一个列关联到表等
 *
 * @author issuser
 */
@Data
public class TableRelationship {
    /**
     * 一对一
     */
    private boolean one2One;

    /**
     * 附表关联列信息
     */
    private ColumnInfo relationColumnInfo;

    /**
     * 表名
     */
    private TableInfo relationTable;

}
