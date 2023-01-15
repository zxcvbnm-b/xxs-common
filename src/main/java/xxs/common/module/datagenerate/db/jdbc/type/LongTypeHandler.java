
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class LongTypeHandler extends BaseTypeHandler<Long> {

    @Override
    protected Long getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        return Math.abs(random.nextLong());
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Long parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setLong(i, parameter);
    }

}
