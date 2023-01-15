
package xxs.common.module.datagenerate.db.jdbc.type;

import org.springframework.util.CollectionUtils;
import xxs.common.module.datagenerate.db.dto.JdbcTypeInfo;
import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.utils.random.RandomNumber;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class BigDecimalTypeHandler extends BaseTypeHandler<BigDecimal> {

    @Override
    protected BigDecimal getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        BigDecimal decimal = new BigDecimal(0);
        JdbcTypeInfo jdbcTypeInfo = tableColumnInfo.getJdbcTypeInfo();
        if (jdbcTypeInfo != null) {
            List<Object> infoArguments = jdbcTypeInfo.getArguments();
            if (!CollectionUtils.isEmpty(infoArguments)) {
                Random random = new Random();
                decimal = new BigDecimal(RandomNumber.getStringRandomByDigit(random.nextInt((int) infoArguments.get(0))));
            }

        }
        return decimal;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BigDecimal parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setBigDecimal(i, parameter);
    }
}
