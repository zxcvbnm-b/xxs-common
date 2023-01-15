package xxs.common.module.config.enumconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

//在进行传输的时候 不管是fastjson，还是jackjson都是使用的是枚举类的名称做valueOf转换成对应的对象，这个类用于反序列化的时候进行通用的对象转枚举的转换
//枚举类：enum { MAN("MAN","男")}
// 所以后端一个属性使用枚举类接收时只能使用一个字符串，比如"sex":"man"，而不能是"sex":{"value":"man","text":"男"}
public class OptionsEnumDeserializer extends JsonDeserializer<Enum> implements ContextualDeserializer {
    private Class clazz;

    public OptionsEnumDeserializer() {
    }

    public Enum deserialize(JsonParser var1, DeserializationContext var2) throws IOException {
        JsonToken var3 = var1.getCurrentToken();
        if (JsonToken.VALUE_STRING.equals(var3) && StringUtils.isNotBlank(var1.getText())) {
            return Enum.valueOf(this.clazz, var1.getText());
        } else if (JsonToken.START_OBJECT.equals(var3)) {
            JsonNode var4 = (JsonNode) var1.getCodec().readTree(var1);
            return this.getEnumValue(var4, this.clazz);
        } else {
            return null;
        }
    }

    private Enum getEnumValue(JsonNode var1, Class var2) {
        if (var1 != null) {
            JsonNode var3 = var1.get("value");
            if (var3 != null && StringUtils.isNotBlank(var3.asText())) {
                return Enum.valueOf(var2, var3.asText());
            }
        }

        return null;
    }

    public JsonDeserializer<?> createContextual(DeserializationContext var1, BeanProperty var2) throws JsonMappingException {
        /* Class var3 = var1.getContextualType().getRawClass();*/
        //TODO 应该是上面这行的????
        Class var3 = var2.getClass();
        OptionsEnumDeserializer var4 = new OptionsEnumDeserializer();
        var4.setClazz(var3);
        return var4;
    }

    public void setClazz(Class var1) {
        this.clazz = var1;
    }
}

