package com.example.create_db_file.controller.form.parts.validator;

import com.example.create_db_file.controller.form.parts.NumberType;
import com.example.create_db_file.controller.form.parts.TimeType;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public class TemporalConfirmValidator implements ConstraintValidator<TemporalConfirm, Object> {

    private String message;

    private String field;

    private String minName;

    private String maxName;

    public void initialize(TemporalConfirm constraintAnnotation) {
        message = constraintAnnotation.message();
        field = StringUtils.hasText(constraintAnnotation.field()) ?
                constraintAnnotation.field() : "timeType";
        minName = constraintAnnotation.minName();
        maxName = constraintAnnotation.maxName();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        TimeType timeType = (TimeType) beanWrapper.getPropertyValue(field);

        if(timeType != TimeType.Between){
            return true;
        }


        Class<?> clazz = beanWrapper.getPropertyType(minName);

        boolean isError = true;

        if(clazz.isAssignableFrom(LocalDateTime.class)){
            LocalDateTime minTime = (LocalDateTime) beanWrapper.getPropertyValue(minName);
            LocalDateTime maxTime = (LocalDateTime) beanWrapper.getPropertyValue(maxName);
            isError = maxTime.isBefore(minTime);
        }else if(clazz.isAssignableFrom(LocalDate.class)){
            LocalDate minTime = (LocalDate) beanWrapper.getPropertyValue(minName);
            LocalDate maxTime = (LocalDate) beanWrapper.getPropertyValue(maxName);
            isError = maxTime.isBefore(minTime);
        }else if(clazz.isAssignableFrom(LocalTime.class)){
            LocalTime minTime = (LocalTime) beanWrapper.getPropertyValue(minName);
            LocalTime maxTime = (LocalTime) beanWrapper.getPropertyValue(maxName);
            isError = maxTime.isBefore(minTime);
        }

        if (isError){
            Temporal minTime = (Temporal) beanWrapper.getPropertyValue(minName);
            Temporal maxTime = (Temporal) beanWrapper.getPropertyValue(maxName);

            message = "最小値・最大値の値が異常です。 min: " + minTime + " > max: " + maxTime;

            context.disableDefaultConstraintViolation(); // (3) デフォルトのConstraintViolationオブジェクトの生成を無効にする。
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("minTime").addConstraintViolation(); // (4)
            return false;
        }

        return true;

    }
}
