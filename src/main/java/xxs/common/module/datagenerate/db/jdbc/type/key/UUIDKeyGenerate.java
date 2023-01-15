package xxs.common.module.datagenerate.db.jdbc.type.key;

import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;

import java.util.UUID;

/*字符串类型的主键生成策略*/
public class UUIDKeyGenerate implements KeyGenerate<String> {
    @Override
    public boolean support(JdbcType jdbcType) {
        if (jdbcType.equals(JdbcType.CHAR) || jdbcType.equals(JdbcType.VARCHAR)) {
            return true;
        }
        return false;
    }

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
