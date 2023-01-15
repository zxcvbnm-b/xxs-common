package xxs.common.module.config.enumconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import xxs.common.module.config.enumconfig.OptionsEnum;
import xxs.common.module.config.enumconfig.OptionsEnumDeserializer;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/*日期序列化统一配置*/
@Configuration
public class JsonSerializerConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.deserializerByType(OptionsEnum.class, new OptionsEnumDeserializer());
        return builder;
    }

    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper;
    }

}
