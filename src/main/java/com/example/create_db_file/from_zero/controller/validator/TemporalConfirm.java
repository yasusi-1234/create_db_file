package com.example.create_db_file.from_zero.controller.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = { TemporalConfirmValidator.class })
@Retention(RetentionPolicy.RUNTIME)
public @interface TemporalConfirm {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Field name
     */
    String field() default "";

    String minName() default "minTime";

    String maxName() default  "maxTime";

//    /**
//     * Class
//     */
//    Class<? extends Temporal> classType() default Temporal.class;

    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        NumberConfirm[] value();
    }
}
