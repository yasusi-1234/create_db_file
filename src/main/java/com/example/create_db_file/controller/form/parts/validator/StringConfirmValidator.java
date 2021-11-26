package com.example.create_db_file.controller.form.parts.validator;

import com.example.create_db_file.controller.form.parts.StringDataForm;
import com.example.create_db_file.controller.form.parts.StringType;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StringConfirmValidator implements ConstraintValidator<StringConfirm, StringDataForm> {

    private String message;

    private String field;

    @Override
    public void initialize(StringConfirm constraintAnnotation) {
        message = StringUtils.hasText(constraintAnnotation.message()) ?
                constraintAnnotation.message()
                : "入力情報に問題があります。値を修正してください。";
        field = StringUtils.hasText(constraintAnnotation.field()) ?
                constraintAnnotation.field()
                : "columnName";
    }

    @Override
    public boolean isValid(StringDataForm value, ConstraintValidatorContext context) {
        StringType stringType = value.getStringType();

        boolean noProblem = true;
        switch (stringType){
            case Between:
                noProblem = checkBetween(value);
                break;
            case Fixed:
                noProblem = checkFixed(value);
                break;
        }

        if(noProblem){
           return true;
        }else{
            context.disableDefaultConstraintViolation(); // (3) デフォルトのConstraintViolationオブジェクトの生成を無効にする。
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field).addConstraintViolation(); // (4)
            return false;
        }
    }

    private boolean checkFixed(StringDataForm form){
        if(StringUtils.hasText(form.getFixedString())){
            return true;
        }else{
            this.message = "必要な文字列が設定されていません。設定してください。";
            return false;
        }
    }

    private boolean checkBetween(StringDataForm form){
        int min = form.getMinLength();
        int max = form.getMaxLength();

        if(min > max){
            this.message = "最小値の値と最大値の値に異常があります min: " + min + " max: " + max;
            return false;
        }else{
            return true;
        }
    }
}
