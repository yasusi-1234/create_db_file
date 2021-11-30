package com.example.create_db_file.from_file.controller.validator;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class DBColumnsValidator implements ConstraintValidator<DBColumns, List<DBColumn>> {
    @Override
    public void initialize(DBColumns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * カラム情報の {@link DBColumn} に一つ以上の include {@code true} がある場合は正常値を
     * 返却、問題がある場合は {@code false} を返却
     * @param value カラムのリスト情報 {@link DBColumnsForm} {@link DBColumn}
     * @param context
     * @return 異常な値 {@code false} 問題無し {@code true}
     */
    @Override
    public boolean isValid(List<DBColumn> value, ConstraintValidatorContext context) {
        if(value.isEmpty()){
            return false;
        }

        for(DBColumn col: value){
            if(col.isInclude()){
                return true;
            }
        }
        return false;
    }
}
