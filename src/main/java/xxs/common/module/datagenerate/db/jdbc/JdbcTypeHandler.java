package xxs.common.module.datagenerate.db.jdbc;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.datagenerate.db.jdbc.type.TypeHandler;
import xxs.common.module.datagenerate.db.jdbc.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcTypeHandler {
    private final static TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    public static TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public void handler(PreparedStatement ps, int i, Object parameter, TableColumnInfo tableColumnInfo) throws SQLException {
        TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(tableColumnInfo.getJdbcTypeInfo().getJdbcType());
        typeHandler.setParameter(ps, i, parameter, tableColumnInfo);

    }

    private JdbcTypeHandler() {
    }
}
