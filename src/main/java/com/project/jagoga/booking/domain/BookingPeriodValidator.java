package com.project.jagoga.booking.domain;

import com.project.jagoga.booking.presentation.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingPeriodValidator implements ConstraintValidator<BookingPeriod, BookingRequestDto> {

    @Override
    public void initialize(BookingPeriod constraintAnnotation) {

    }

    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext context) {
        return bookingRequestDto.isValidPeriod();
    }
}
