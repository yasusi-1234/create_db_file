package com.example.create_db_file.controller.form.parts.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = { NumberConfirmValidator.class })
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberConfirm {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Field name
     */
    String field() default "";

    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        NumberConfirm[] value();
    }
}
