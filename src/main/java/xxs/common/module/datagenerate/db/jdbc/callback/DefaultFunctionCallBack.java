package xxs.common.module.datagenerate.db.jdbc.callback;


import net.datafaker.Faker;
import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 列参数值值生成回调  可以引入datafaker 进行数据生成 （生成地址，名字什么的，手机号）
 *
 * @author issuser
 */
public class DefaultFunctionCallBack {
    private final List<FunctionCallBack> functionCallBacks = new ArrayList<>();

    public DefaultFunctionCallBack() {
        functionCallBacks.add(new KeyFunctionCallBack());
    }

    public Function getFunctionCallBack(TableColumnInfo tableColumnInfo) {
        for (FunctionCallBack functionCallBack : functionCallBacks) {
            if (functionCallBack.support(tableColumnInfo)) {
                return functionCallBack.generate();
            }
        }
        return null;
    }

    public DefaultFunctionCallBack addFunctionCallBack(FunctionCallBack functionCallBack) {
        functionCallBacks.add(functionCallBack);
        return this;
    }

    public static void main(String[] args) {
        Faker faker = new Faker();
        System.out.println(faker.address().city());
        DefaultFunctionCallBack back = new DefaultFunctionCallBack();
        back.addFunctionCallBack(new FunctionCallBack<TableColumnInfo>() {

            @Override
            public boolean support(TableColumnInfo source) {
                return false;
            }

            @Override
            public Function<TableColumnInfo, Object> generate() {
                return null;
            }
        }).addFunctionCallBack(new FunctionCallBack<TableColumnInfo>() {
            @Override
            public boolean support(TableColumnInfo source) {
                return false;
            }

            @Override
            public Function<TableColumnInfo, Object> generate() {
                return null;
            }
        });
    }
}
