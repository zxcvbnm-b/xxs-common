package xxs.common.module.datagenerate.db.jdbc.callback;

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.datagenerate.db.jdbc.type.key.RouteKeyGenerate;

import java.util.function.Function;

/*主键列值回调*/
public class KeyFunctionCallBack implements FunctionCallBack<TableColumnInfo> {
    @Override
    public boolean support(TableColumnInfo source) {
        if (source.isPrimaryKey()) {
            return true;
        }
        return false;
    }

    @Override
    public Function<TableColumnInfo, Object> generate() {
        return (tableColumnInfo -> {
            return RouteKeyGenerate.getKeyGenerate(tableColumnInfo.getJdbcTypeInfo().getJdbcType()).generate();
        });
    }
}
