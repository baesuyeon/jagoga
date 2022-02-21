package com.project.jagoga.accommodation.presentation.dto;

import com.project.jagoga.accommodation.domain.AccommodationType;
import com.project.jagoga.accommodation.domain.address.City;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@Getter
public class AccommodationUpdateRequestDto {

    @NotBlank(message = "형식이 맞지 않습니다")
    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}")
    private String phoneNumber;

    @NotNull(message = "주소가 빈 칸일 수 없습니다.")
    private City city;

    @NotNull(message = "숙소 타입을 선택해주세요.")
    private AccommodationType accommodationType;

    private String description;
    private String information;
    private int lowPrice;
}
