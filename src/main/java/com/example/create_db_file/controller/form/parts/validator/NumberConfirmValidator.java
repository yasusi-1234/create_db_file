package com.example.create_db_file.controller.form.parts.validator;

import com.example.create_db_file.controller.form.parts.NumberType;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NumberConfirmValidator implements ConstraintValidator<NumberConfirm, Object> {

    private String message;

    private String field;

    public void initialize(NumberConfirm constraintAnnotation) {
        message = constraintAnnotation.message();
        field = StringUtils.hasText(constraintAnnotation.field()) ?
        constraintAnnotation.field() : "numberType";
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        NumberType numberType = (NumberType) beanWrapper.getPropertyValue(field);
        long minNum = (long) beanWrapper.getPropertyValue("minNumber");
        long maxNum = (long) beanWrapper.getPropertyValue("maxNumber");

        boolean matched = true;

        if(numberType == NumberType.Between){
            matched = minNum <= maxNum;
            message = "最小値・最大値の値が異常です。 min: " + minNum + " > max: " + maxNum;
        }

        if (matched) {
            return true;
        } else {
            context.disableDefaultConstraintViolation(); // (3) デフォルトのConstraintViolationオブジェクトの生成を無効にする。
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field).addConstraintViolation(); // (4)
            return false;
        }
    }
}
