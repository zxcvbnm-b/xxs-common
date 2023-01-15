package xxs.common.module.datagenerate.db.jdbc.callback;

import java.sql.JDBCType;
import java.util.function.Function;

public interface FunctionCallBack<T> {
    boolean support(T source);

    Function<T, Object> generate();
}
