
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class IntegerTypeHandler extends BaseTypeHandler<Integer> {

    @Override
    protected Integer getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        return Math.abs(random.nextInt());
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Integer parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setInt(i, parameter);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            System.out.println(Math.abs(random.nextInt()));
        }
    }
}
