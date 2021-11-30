package com.example.create_db_file.from_zero.controller.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = { AllListConfirmValidator.class })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllListConfirm {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field() default "";

    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        AllListConfirm[] value();
    }
}
