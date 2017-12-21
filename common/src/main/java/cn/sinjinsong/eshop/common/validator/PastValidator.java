package cn.sinjinsong.eshop.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Created by SinjinSong on 2017/10/11.
 */
public class PastValidator implements ConstraintValidator<Past, LocalDate> {

    @Override
    public void initialize(Past constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (value.isBefore(LocalDate.now())) {
            return true;
        }
        return false;
    }
}
