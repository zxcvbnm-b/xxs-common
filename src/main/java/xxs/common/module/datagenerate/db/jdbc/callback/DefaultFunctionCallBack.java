package xxs.common.module.datagenerate.db.jdbc.callback;


import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/*列参数值值生成回调*/
public class DefaultFunctionCallBack {
    private static final DefaultFunctionCallBack defaultFunctionCallBack = new DefaultFunctionCallBack();
    private static final List<FunctionCallBack> functionCallBacks = new ArrayList<>();

    private DefaultFunctionCallBack() {
        functionCallBacks.add(new KeyFunctionCallBack());
    }

    public static DefaultFunctionCallBack getDefaultFunctionCallBack() {
        return defaultFunctionCallBack;
    }

    public static Function getFunctionCallBack(TableColumnInfo tableColumnInfo) {
        for (FunctionCallBack functionCallBack : functionCallBacks) {
            if (functionCallBack.support(tableColumnInfo)) {
                return functionCallBack.generate();
            }
        }
        return null;
    }
}
