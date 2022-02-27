package com.project.jagoga.booking.domain;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BookingPeriodValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingPeriod {

    String message() default "유효하지 않은 기간입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}