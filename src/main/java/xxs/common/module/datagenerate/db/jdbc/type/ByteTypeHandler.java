
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class ByteTypeHandler extends BaseTypeHandler<Byte> {

    @Override
    protected Byte getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        return Byte.valueOf(Integer.toString(Math.abs(random.nextInt(127))));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Byte parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setByte(i, parameter);
    }
}
