package xxs.common.module.codegenerate.model;

import java.lang.annotation.*;

/**
 * @author xiongsongxu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TableColumn {
    String fieldName();
}
