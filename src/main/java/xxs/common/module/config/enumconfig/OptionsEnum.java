package xxs.common.module.config.enumconfig;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;

@JsonDeserialize(
        using = OptionsEnumDeserializer.class
)
public interface OptionsEnum extends Serializable {
    String getText();
}
