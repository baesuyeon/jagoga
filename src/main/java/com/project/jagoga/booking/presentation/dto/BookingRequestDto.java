package com.project.jagoga.booking.presentation.dto;

import com.project.jagoga.booking.domain.Booking;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;

import com.project.jagoga.booking.domain.BookingPeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@BookingPeriod
public class BookingRequestDto {

    @NotNull(message = "체크인 날짜를 지정해야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @NotNull(message = "체크아웃 날짜를 지정해야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    public Booking toEntity(long userId, long roomTypeId) {
        return Booking.createInstance(userId, roomTypeId, checkInDate, checkOutDate);
    }

    public boolean isValidPeriod() {
        if (checkInDate == null || checkOutDate == null) {
            return false;
        }

        return checkOutDate.isAfter(checkInDate);
    }
}
