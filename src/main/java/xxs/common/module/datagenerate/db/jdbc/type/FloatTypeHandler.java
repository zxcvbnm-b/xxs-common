
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class FloatTypeHandler extends BaseTypeHandler<Float> {

    @Override
    protected Float getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        return Math.abs(random.nextFloat());
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Float parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setFloat(i, parameter);
    }
}
