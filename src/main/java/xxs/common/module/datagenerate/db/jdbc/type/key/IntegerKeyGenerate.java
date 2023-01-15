package xxs.common.module.datagenerate.db.jdbc.type.key;

import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;

import java.util.concurrent.atomic.AtomicInteger;

/*int类型的主键生成策略*/
public class IntegerKeyGenerate implements KeyGenerate<Integer> {
    private AtomicInteger atomicInteger = new AtomicInteger(1000000);

    @Override
    public boolean support(JdbcType jdbcType) {
        if (jdbcType.equals(JdbcType.INTEGER) || jdbcType.equals(JdbcType.FLOAT) || jdbcType.equals(JdbcType.DOUBLE)) {
            return true;
        }
        return false;
    }

    @Override
    public Integer generate() {
        return atomicInteger.incrementAndGet();
    }
}
