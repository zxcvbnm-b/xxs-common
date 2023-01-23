package xxs.common.module.codegenerate.model;

import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import lombok.Data;
import xxs.common.module.codegenerate.TypeMapperRegistry;

@Data
public class ColumnInfo {
    /*是否主键*/
    private boolean keyFlag;
    /*列名*/
    private String columnName;
    //在获取时就进行小驼峰
    private String propertyName;
    //首字母大写
    private String capitalizePropertyName;
    /*jdbc类型名称*/
    private String jdbcTypeName;
    /*java类型*/
    private Class javaType;
    /*jdbc类型code*/
    private int jdbcTypeCode;
    /*备注*/
    private String comment;
    /*是否可以为空*/
    private boolean nullAble;
    /*是否是自增*/
    private boolean autoincrement;
    /*列的最大大小*/
    private int columnSize;


    public Class getJavaType() {
        return TypeMapperRegistry.getJavaType(jdbcTypeCode);
    }

    public static void main(String[] args) {
        Class clazz = String.class;
        System.out.println(clazz.getSimpleName());
    }
}
