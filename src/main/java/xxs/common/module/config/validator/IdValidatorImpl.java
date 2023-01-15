package xxs.common.module.config.validator;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class IdValidatorImpl implements ConstraintValidator<IdValidator, Long> {
    /*@Autowired*/
    private String dealerMasterApi;

    private IdValidatorType idValidatorType;


    @Override
    public void initialize(IdValidator idValidator) {
        idValidatorType = idValidator.idValidatorType();
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        //验证逻辑。
        return true;
    }
}
