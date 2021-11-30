package com.example.create_db_file.from_zero.controller.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = { StringConfirmValidator.class })
@Retention(RetentionPolicy.RUNTIME)
public @interface StringConfirm {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Field name
     */
    String field() default "columnName";

    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        StringConfirm[] value();
    }
}
