package xxs.common.module.datagenerate.db.jdbc.type.key;

import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;
import xxs.common.module.utils.other.LongCodeGenerateUtils;

/*long类型的主键生成策略*/
public class LongKeyGenerate implements KeyGenerate<Long> {
    @Override
    public boolean support(JdbcType jdbcType) {
        if (JdbcType.BIGINT.equals(jdbcType)) {
            return true;
        }
        return false;
    }

    @Override
    public Long generate() {
        return LongCodeGenerateUtils.getInstance().genCode();
    }
}
