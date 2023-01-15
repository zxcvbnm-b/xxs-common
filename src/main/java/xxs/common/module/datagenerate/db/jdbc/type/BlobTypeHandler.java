
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.utils.random.RandomString;

import java.io.ByteArrayInputStream;
import java.sql.*;

public class BlobTypeHandler extends BaseTypeHandler<byte[]> {

    @Override
    protected byte[] getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        String randomChineseString = RandomString.getRandomChineseString(10);
        return randomChineseString.getBytes();
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, byte[] parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ByteArrayInputStream bis = new ByteArrayInputStream(parameter);
        ps.setBinaryStream(i, bis, parameter.length);
    }
}