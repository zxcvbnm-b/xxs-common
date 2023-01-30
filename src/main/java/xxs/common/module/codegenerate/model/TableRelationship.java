package xxs.common.module.codegenerate.model;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class TableRelationship {
    /*一对一*/
    private boolean one2One;

    /*主表关联列信息--一般就是使用主键关联 TODO  第一阶段只支持以主表主键作为关联*/
    private ColumnInfo masterColumnInfo;

    /*附表关联列信息*/
    private ColumnInfo relationColumnInfo;

    /*表名*/
    private TableInfo relationTable;

}
