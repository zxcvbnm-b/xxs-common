
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class ShortTypeHandler extends BaseTypeHandler<Short> {

    @Override
    protected Short getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        return Short.valueOf(Integer.toString(Math.abs(random.nextInt(32767))));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Short parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setShort(i, parameter);
    }
}
