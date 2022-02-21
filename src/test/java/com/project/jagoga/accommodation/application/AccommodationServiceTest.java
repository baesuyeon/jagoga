package com.project.jagoga.accommodation.application;

import com.project.jagoga.accommodation.domain.Accommodation;
import com.project.jagoga.accommodation.domain.AccommodationRepository;
import com.project.jagoga.accommodation.domain.AccommodationType;
import com.project.jagoga.accommodation.domain.address.City;
import com.project.jagoga.accommodation.domain.address.State;
import com.project.jagoga.accommodation.presentation.dto.AccommodationRequestDto;
import com.project.jagoga.accommodation.presentation.dto.AccommodationUpdateRequestDto;
import com.project.jagoga.exception.accommodation.DuplicatedAccommodationException;
import com.project.jagoga.exception.accommodation.NotExistAccommodationException;
import com.project.jagoga.exception.user.ForbiddenException;
import com.project.jagoga.user.domain.AuthUser;
import com.project.jagoga.user.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @InjectMocks
    private AccommodationService accommodationService;

    @Mock
    private AccommodationRepository accommodationRepository;

    public static AuthUser createOwnerAuthUser() {
        String email = "test@test";
        return AuthUser.createInstance(1L, email, Role.OWNER);
    }

    public static AuthUser createBasicAuthUser() {
        String email = "test@test";
        return AuthUser.createInstance(2L, email, Role.BASIC);
    }

    public static AccommodationRequestDto createAccommodationRequestDto() {
        return AccommodationRequestDto.builder()
                .accommodationName("name").accommodationType(AccommodationType.PENSION).phoneNumber("010-1234-1234")
                .city(City.builder().id(1L).name("city").state(new State(1L, "state")).categoryId(1L).build())
                .build();
    }

    public static AccommodationUpdateRequestDto createAccommodationUpdateRequestDto() {
        return new AccommodationUpdateRequestDto("010-4321-1234",
                new City(1L, "city", new State(1L, "statte"), 1L),
                AccommodationType.HOTEL, "update", "update", 1000);
    }

    @DisplayName("OWNER 권한을 가진 사용자는 숙소를 등록할 수 있다.")
    @Test
    void succeed_Register_Accommodation_ByOwner() {
        // given
        AccommodationRequestDto dto = createAccommodationRequestDto();
        when(accommodationRepository.findByAccommodationName(dto.getAccommodationName())).thenReturn(Optional.empty());

        // when
        accommodationService.saveAccommodation(dto, createOwnerAuthUser());

        // then
        verify(accommodationRepository, times(1)).save(any(Accommodation.class));
    }

    @DisplayName("이미 등록된 숙소 등록 시 예외가 발생한다.")
    @Test
    void should_Fail_Register_ExistAccommodation() {
        // given
        AccommodationRequestDto dto = createAccommodationRequestDto();
        when(accommodationRepository.findByAccommodationName(dto.getAccommodationName()))
                .thenReturn(Optional.of(dto.toEntity(1L)));

        // then
        Exception exception = assertThrows(DuplicatedAccommodationException.class,
                // when
                () -> accommodationService.saveAccommodation(dto, createOwnerAuthUser()));

        // then
        assertEquals("이미 존재하는 상품입니다.", exception.getMessage());
    }

    @DisplayName("일반 사용자는 숙소를 등록할 수 없다.")
    @Test
    void should_Fail_Register_Accommodation_ByBasicUser() {
        // given
        AccommodationRequestDto dto = createAccommodationRequestDto();

        // then
        Exception exception = assertThrows(ForbiddenException.class,
                // when
                () -> accommodationService.saveAccommodation(dto, createBasicAuthUser()));

        // then
        assertEquals("권한이 없는 사용자입니다", exception.getMessage());
    }

    @DisplayName("숙소 Owner가 숙소 정보를 수정한다.")
    @Test
    void succeed_Update_Accommodation_ByOwner() {
        // given
        AccommodationUpdateRequestDto dto = createAccommodationUpdateRequestDto();
        when(accommodationRepository.findById(1L))
                .thenReturn(Optional.of(createAccommodationRequestDto().toEntity(1L)));

        // when
        Accommodation updatedAccommodation = accommodationService.updateAccommodation(
                1L,
                createAccommodationUpdateRequestDto(),
                createOwnerAuthUser());

        // then
        assertThat(createAccommodationRequestDto().toEntity(1L).getAccommodationName())
                .isEqualTo(updatedAccommodation.getAccommodationName());
        assertThat(dto.getAccommodationType()).isEqualTo(updatedAccommodation.getAccommodationType());
        assertThat(dto.getCity().getId()).isEqualTo(updatedAccommodation.getCityId());
        assertThat(dto.getPhoneNumber()).isEqualTo(updatedAccommodation.getPhoneNumber());
        assertThat(dto.getInformation()).isEqualTo(updatedAccommodation.getInformation());
        assertThat(dto.getDescription()).isEqualTo(updatedAccommodation.getDescription());
        assertThat(dto.getLowPrice()).isEqualTo(updatedAccommodation.getLowPrice());
    }

    @DisplayName("관리자는 숙소 정보를 수정할 수 있다.")
    @Test
    void updateAccommodationByAdmin_Success() {
        // given
        AccommodationUpdateRequestDto dto = createAccommodationUpdateRequestDto();
        when(accommodationRepository.findById(1L))
                .thenReturn(Optional.of(createAccommodationRequestDto().toEntity(1L)));

        // when
        Accommodation updatedAccommodation = accommodationService.updateAccommodation(
                1L,
                createAccommodationUpdateRequestDto(),
                createOwnerAuthUser());

        // then
        assertThat(createAccommodationRequestDto().toEntity(1L).getAccommodationName())
                .isEqualTo(updatedAccommodation.getAccommodationName());
        assertThat(dto.getAccommodationType()).isEqualTo(updatedAccommodation.getAccommodationType());
        assertThat(dto.getCity().getId()).isEqualTo(updatedAccommodation.getCityId());
        assertThat(dto.getPhoneNumber()).isEqualTo(updatedAccommodation.getPhoneNumber());
        assertThat(dto.getInformation()).isEqualTo(updatedAccommodation.getInformation());
        assertThat(dto.getDescription()).isEqualTo(updatedAccommodation.getDescription());
        assertThat(dto.getLowPrice()).isEqualTo(updatedAccommodation.getLowPrice());
    }

    @DisplayName("존재하지 않는 숙소 정보를 수정시 예외가 발생한다.")
    @Test
    void should_Fail_Update_NotExist_Accommodation() {
        // given
        AccommodationUpdateRequestDto dto = createAccommodationUpdateRequestDto();

        // then
        assertThrows(NotExistAccommodationException.class,
                () -> accommodationService.updateAccommodation(1L, dto, createOwnerAuthUser()));
    }

    @DisplayName("숙소 관리자가 아닌 타인이 숙소 정보를 수정할 경우 예외가 발생한다.")
    @Test
    void updateAccommodationByNotOwner_Exception() {
        // given
        AccommodationUpdateRequestDto dto = createAccommodationUpdateRequestDto();
        when(accommodationRepository.findById(1L))
                .thenReturn(Optional.of(createAccommodationRequestDto().toEntity(3L)));

        // then
        Exception exception = assertThrows(ForbiddenException.class,
                // when
                () -> accommodationService.updateAccommodation(
                        1L,
                        createAccommodationUpdateRequestDto(),
                        createOwnerAuthUser()));

        // then
        assertEquals("권한이 없는 사용자입니다", exception.getMessage());
    }
}
