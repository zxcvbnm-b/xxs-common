package xxs.common.module.config.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

/**
 * jsr303验证注解--远程调用判断这个id的实体存不存在----
 * 比如在使用时@IdValidator(idValidatorType=IdValidatorType.USER,message="业务分类不能存在")
 */

@Documented
@Constraint(validatedBy = IdValidatorImpl.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotNull
public @interface IdValidator {
    IdValidatorType idValidatorType();

    String message() default "ID不存在";//错误提示信息

    Class<?>[] groups() default {};//分组

    Class<? extends Payload>[] payload() default {};
}