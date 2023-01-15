
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, TableColumnInfo tableColumnInfo) throws SQLException {
        try {
            if (parameter == null) {
                parameter = getParameterByTableColumnInfo(tableColumnInfo);
            }
            setNonNullParameter(ps, i, parameter, tableColumnInfo);
        } catch (Exception e) {
            throw new RuntimeException("Cause: " + e, e);
        }
    }

    protected abstract T getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo);

    public abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, TableColumnInfo tableColumnInfo) throws SQLException;

}
