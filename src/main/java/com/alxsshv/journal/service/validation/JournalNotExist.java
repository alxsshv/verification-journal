package com.alxsshv.journal.service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JournalConstraintValidator.class)
public @interface JournalNotExist {
    String message() default "Журнал с таким номером уже существует";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
