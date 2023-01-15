
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class BooleanTypeHandler extends BaseTypeHandler<Boolean> {

    @Override
    protected Boolean getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        int i = random.nextInt(10);
        return i % 2 == 1;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setBoolean(i, parameter);
    }
}
