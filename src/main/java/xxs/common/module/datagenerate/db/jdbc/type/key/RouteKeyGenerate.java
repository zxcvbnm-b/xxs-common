package xxs.common.module.datagenerate.db.jdbc.type.key;

import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;

import java.util.ArrayList;
import java.util.List;

public class RouteKeyGenerate {
    private static final RouteKeyGenerate routeKeyGenerate = new RouteKeyGenerate();
    private static final List<KeyGenerate> keyGenerates = new ArrayList<>();

    private RouteKeyGenerate() {
        keyGenerates.add(new IntegerKeyGenerate());
        keyGenerates.add(new LongKeyGenerate());
        keyGenerates.add(new UUIDKeyGenerate());
    }

    public static RouteKeyGenerate getRouteKeyGenerate() {
        return routeKeyGenerate;
    }

    public static KeyGenerate getKeyGenerate(JdbcType jdbcType) {
        for (KeyGenerate keyGenerate : keyGenerates) {
            if (keyGenerate.support(jdbcType)) {
                return keyGenerate;
            }
        }
        throw new RuntimeException("获取不到key生成策略");
    }
}
