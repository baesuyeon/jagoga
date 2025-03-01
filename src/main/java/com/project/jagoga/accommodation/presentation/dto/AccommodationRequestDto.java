package com.project.jagoga.accommodation.presentation.dto;

import com.project.jagoga.accommodation.domain.Accommodation;
import com.project.jagoga.accommodation.domain.AccommodationType;
import com.project.jagoga.accommodation.domain.address.City;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AccommodationRequestDto {

    @NotBlank(message = "숙소 이름은 빈 칸일 수 없습니다.")
    @Length(max = 20, message = "이름은 20자 이내로 입력하세요.")
    private String accommodationName;

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

    public Accommodation toEntity(long ownerId) {
        return Accommodation.builder()
            .accommodationName(accommodationName)
            .ownerId(ownerId)
            .phoneNumber(phoneNumber)
            .cityId(city.getId())
            .accommodationType(accommodationType)
            .description(description)
            .information(information)
            .lowPrice(lowPrice)
            .build();
    }
}
