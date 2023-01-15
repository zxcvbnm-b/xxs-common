package xxs.common.module.codegenerate.model;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TableInfo {
    private String name;
    private String comment;
    private String tableType;
    //驼峰 tableName
    private String tableName;
    //驼峰首字母大写TableName
    private String capitalizeTableName;
    private List<ColumnInfo> columnInfos;
    private ColumnInfo keyColumnInfo;

    public Set<String> getColumnTypePackageNames(){
        Set<String> result=new HashSet<>();
        if(CollectionUtils.isEmpty(columnInfos)){
            return result;
        }
        for (ColumnInfo columnInfo : columnInfos) {
            result.add(columnInfo.getJavaType().getName());
        }
        return result;
    }
}
