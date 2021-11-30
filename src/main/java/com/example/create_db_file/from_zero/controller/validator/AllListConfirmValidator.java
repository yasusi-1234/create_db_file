package com.example.create_db_file.from_zero.controller.validator;

import com.example.create_db_file.from_zero.controller.form.CreateFromZeroForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AllListConfirmValidator implements ConstraintValidator<AllListConfirm, CreateFromZeroForm> {

    private String message;

    private String field;

    @Override
    public void initialize(AllListConfirm constraintAnnotation) {
        message = constraintAnnotation.message();
        field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(CreateFromZeroForm form, ConstraintValidatorContext context) {

        int columnCount = form.allRequestCount();

        if(columnCount > 0){
            return true;
        }else{
            message = "※ 列が1つも指定されていません。1つ以上選択してください。";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field).addConstraintViolation();
            return false;
        }
    }
}
