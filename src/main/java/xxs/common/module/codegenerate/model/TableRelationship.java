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
     * 附表关联列信息 关联的唯一的裂名 用于在删除的时候可以匹配删除 比如一个用户关联了多个角色，那么删除的时候，更新的时候是根据 roleid去匹配的 而不是id （当然也可以使用id）*
     */
    private ColumnInfo relationUniqueColumnInfo;

    /**
     * 表名
     */
    private TableInfo relationTable;

}
