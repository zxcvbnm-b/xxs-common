
package xxs.common.module.codegenerate;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.util.*;

/**
 * @author xxs
 */
public final class TypeMapperRegistry {
    /**
     * jdbc类型的类型处理器  一个jdbc类型为xxx的可以使用使用什么类型处理器
     */
    private static final Map<Integer, Class> jdbcTypeMapperMap = new HashMap<>();

    static {
        register(JDBCType.BOOLEAN, Boolean.class);
        register(JDBCType.BIT, Boolean.class);

        register(JDBCType.TINYINT, Byte.class);

        register(JDBCType.SMALLINT, Short.class);

        register(JDBCType.INTEGER, Integer.class);

        register(JDBCType.FLOAT, Float.class);
        register(JDBCType.DOUBLE, Double.class);
        register(JDBCType.CHAR, String.class);
        register(JDBCType.VARCHAR, String.class);
        register(JDBCType.CLOB, String.class);
        register(JDBCType.LONGVARCHAR, String.class);
        register(JDBCType.OTHER, String.class);
        register(JDBCType.NVARCHAR, String.class);
        register(JDBCType.NCHAR, String.class);
        register(JDBCType.NCLOB, String.class);
        register(JDBCType.BIGINT, Long.class);
        register(JDBCType.REAL, BigDecimal.class);
        register(JDBCType.DECIMAL, BigDecimal.class);
        register(JDBCType.NUMERIC, BigDecimal.class);

        register(JDBCType.LONGVARBINARY, byte[].class);
        register(JDBCType.BLOB, String.class);
        register(JDBCType.TIMESTAMP, Date.class);
        register(JDBCType.DATE, Date.class);
        register(JDBCType.TIME, Date.class);
    }

    public static void register(JDBCType jdbcType, Class javaType) {
        jdbcTypeMapperMap.put(jdbcType.getVendorTypeNumber(), javaType);
    }

    public static Class getJavaType(Integer code) {
        return jdbcTypeMapperMap.get(code);
    }
}
