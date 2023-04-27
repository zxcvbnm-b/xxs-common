package xxs.common.module.codegenerate.method.returntype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Map
 *
 * @author xxs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListMapReturnType implements ReturnType {
    private String keyString = "String";
    private String valueString = "Object";

    @Override
    public String methodReturnTypeString() {
        return String.format("List<Map<%s,%s>>", keyString, valueString);
    }

    @Override
    public String methodXmlReturnTypeString() {
        return Map.class.getName();
    }
}
