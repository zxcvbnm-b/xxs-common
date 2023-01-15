package xxs.common.module.datagenerate.db.jdbc.type.key;

import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;

/*主键生成策略*/
public interface KeyGenerate<T> {
    boolean support(JdbcType jdbcType);

    T generate();
}
