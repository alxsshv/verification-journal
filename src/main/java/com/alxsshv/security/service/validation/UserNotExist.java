package com.alxsshv.security.service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserNotExistValidator.class)
public @interface UserNotExist {
    String message() default "Пользователь c указанным логином уже существует";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
