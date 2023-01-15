
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.utils.random.RandomString;

import java.io.StringReader;
import java.sql.*;
import java.util.Random;

public class NClobTypeHandler extends BaseTypeHandler<String> {

    @Override
    protected String getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        return RandomString.getRandomChineseString(random.nextInt(100));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        StringReader reader = new StringReader(parameter);
        ps.setCharacterStream(i, reader, parameter.length());
    }

    private String toString(Clob clob) throws SQLException {
        return clob == null ? null : clob.getSubString(1, (int) clob.length());
    }

}