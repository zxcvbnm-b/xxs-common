package xxs.common.module.codegenerate.model;

import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import lombok.Data;
import xxs.common.module.codegenerate.TypeMapperRegistry;

@Data
public class ColumnInfo {
    /*是否主键*/
    private boolean keyFlag;
    private boolean keyIdentityFlag;
    private String columnName;
    //在获取时就进行小驼峰
    private String propertyName;
    //首字母大写
    private String capitalizePropertyName;
    private String jdbcTypeName;
    private Class javaType;
    private int jdbcTypeCode;
    private IColumnType columnType;
    private String comment;
    private boolean nullAble;


    public Class getJavaType() {
        return TypeMapperRegistry.getJavaType(jdbcTypeCode);
    }

    public static void main(String[] args) {
        Class clazz = String.class;
        System.out.println(clazz.getSimpleName());
    }
}
