package com.example.create_db_file.controller.validator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.example.create_db_file.controller.validator.DBColumns.List;

/**
 * {@link com.example.create_db_file.controller.form.DBColumnsForm} の
 * {@link com.example.create_db_file.controller.form.DBColumn} の
 * includeが一つ以上選択されているか確認するためのValidator
 * 処理は{@link DBColumnsValidator#isValid(java.util.List, ConstraintValidatorContext)}を参照
 */
@Documented
@Constraint(validatedBy = {DBColumnsValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface DBColumns {

    String message() default "※出力するカラムを1つ以上選択してください。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DBColumns[] value();
    }
}
