package xxs.common.module.datagenerate.db.jdbc.callback;


import net.datafaker.Faker;
import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * 列参数值值生成回调  可以引入datafaker 进行数据生成 （生成地址，名字什么的，手机号）
 *https://www.datafaker.net/documentation/custom-providers/#usage
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
        Faker faker = new Faker(Locale.CHINA);
        System.out.println(faker.address().city());
        System.out.println(faker.name().fullName());
        DefaultFunctionCallBack back = new DefaultFunctionCallBack();
        back.addFunctionCallBack(new FunctionCallBack<TableColumnInfo>() {
            @Override
            public boolean support(TableColumnInfo source) {
                if(source.getColumnName().equalsIgnoreCase("address")){
                    return true;
                }
                return false;
            }

            @Override
            public Function<TableColumnInfo, Object> generate() {
                return t->{
                    return faker.address().cityName();
                };
            }
        });
    }
}
