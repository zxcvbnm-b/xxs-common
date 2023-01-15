
package xxs.common.module.datagenerate.db.jdbc.type;

import java.lang.reflect.Constructor;
import java.util.*;

public final class TypeHandlerRegistry {
    /*jdbc类型的类型处理器  一个jdbc类型为xxx的可以使用使用什么类型处理器*/
    private final Map<JdbcType, TypeHandler<?>> jdbcTypeHandlerMap = new EnumMap<>(JdbcType.class);

    private static final Map<JdbcType, TypeHandler<?>> NULL_TYPE_HANDLER_MAP = Collections.emptyMap();
    private static IntegerKeyTypeHandler integerKeyTypeHandler = new IntegerKeyTypeHandler();

    /**
     * The constructor that pass the MyBatis configuration.
     *
     * @since 3.5.4
     */
    public TypeHandlerRegistry() {
        register(JdbcType.BOOLEAN, new BooleanTypeHandler());
        register(JdbcType.BIT, new BooleanTypeHandler());

        register(JdbcType.TINYINT, new ByteTypeHandler());

        register(JdbcType.SMALLINT, new ShortTypeHandler());

        register(JdbcType.INTEGER, new IntegerTypeHandler());

        register(JdbcType.FLOAT, new FloatTypeHandler());
        register(JdbcType.DOUBLE, new DoubleTypeHandler());
        register(JdbcType.CHAR, new StringTypeHandler());
        register(JdbcType.VARCHAR, new StringTypeHandler());
        register(JdbcType.CLOB, new ClobTypeHandler());
        register(JdbcType.LONGVARCHAR, new StringTypeHandler());
        register(JdbcType.OTHER, new StringTypeHandler());
        register(JdbcType.NVARCHAR, new NStringTypeHandler());
        register(JdbcType.NCHAR, new NStringTypeHandler());
        register(JdbcType.NCLOB, new NClobTypeHandler());
        register(JdbcType.BIGINT, new LongTypeHandler());
        register(JdbcType.REAL, new BigDecimalTypeHandler());
        register(JdbcType.DECIMAL, new BigDecimalTypeHandler());
        register(JdbcType.NUMERIC, new BigDecimalTypeHandler());

        register(JdbcType.LONGVARBINARY, new BlobTypeHandler());
        register(JdbcType.BLOB, new BlobTypeHandler());
        register(JdbcType.TIMESTAMP, new DateTypeHandler());
        register(JdbcType.DATE, new DateOnlyTypeHandler());
        register(JdbcType.TIME, new TimeOnlyTypeHandler());
    }


    public TypeHandler<?> getTypeHandler(JdbcType jdbcType) {
        return jdbcTypeHandlerMap.get(jdbcType);
    }

    public void register(JdbcType jdbcType, TypeHandler<?> handler) {
        jdbcTypeHandlerMap.put(jdbcType, handler);
    }

    public static IntegerKeyTypeHandler getIntegerKeyTypeHandler() {
        return integerKeyTypeHandler;
    }

    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
            try {
                Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
                return (TypeHandler<T>) c.newInstance(javaTypeClass);
            } catch (NoSuchMethodException ignored) {
                // ignored
            } catch (Exception e) {
                throw new RuntimeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }
}
