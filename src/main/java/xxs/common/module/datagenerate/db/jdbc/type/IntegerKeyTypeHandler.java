
package xxs.common.module.datagenerate.db.jdbc.type;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 主键生成 这个初始值应该是做配置的。不然有可能会主键冲突
 */
public class IntegerKeyTypeHandler extends BaseTypeHandler<Integer> {
    private AtomicInteger atomicInteger = new AtomicInteger(1000000);

    @Override
    public Integer getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        return atomicInteger.incrementAndGet();
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
