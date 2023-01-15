
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

public class DateOnlyTypeHandler extends BaseTypeHandler<Date> {

    @Override
    protected Date getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        return new Date(new Date().getTime() - random.nextInt(200000));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setDate(i, new java.sql.Date(parameter.getTime()));
    }
}
