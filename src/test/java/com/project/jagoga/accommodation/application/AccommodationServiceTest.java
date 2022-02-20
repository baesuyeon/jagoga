package com.project.jagoga.accommodation.application;

import com.project.jagoga.accommodation.domain.Accommodation;
import com.project.jagoga.accommodation.domain.AccommodationRepository;
import com.project.jagoga.accommodation.domain.AccommodationType;
import com.project.jagoga.accommodation.domain.address.City;
import com.project.jagoga.accommodation.domain.address.State;
import com.project.jagoga.accommodation.presentation.dto.AccommodationRequestDto;
import com.project.jagoga.exception.accommodation.DuplicatedAccommodationException;
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
        return AuthUser.createInstance(1L, email, Role.BASIC);
    }

    public static AccommodationRequestDto createAccommodationRequestDto() {
        return AccommodationRequestDto.builder()
                .accommodationName("name").accommodationType(AccommodationType.PENSION)
                .city(City.builder().id(1L).name("city").state(new State(1L, "state")).categoryId(1L).build())
                .build();
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

    /*
    @DisplayName("숙소 정보를 수정한다.")
    @Test
    void succeed_Update_Accommodation_ByOwner() {
        // given

        // AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        // Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        // AccommodationRequestDto accommodation2 = AccommodationFactory.mockUpdatedAccommodationRequestDto(city);



        // when
        Accommodation updatedAccommodation
                = accommodationService.updateAccommodation(
                savedAccommodation.getId(), accommodation2, authUser);

        // then

        Accommodation findAccommodation = accommodationService.getAccommodationById(updatedAccommodation.getId());
        assertThat(accommodation.getAccommodationName())
                .isNotEqualTo(findAccommodation.getAccommodationName());
    }

    /*



    @DisplayName("숙소 정보를 수정한다.")
    @Test
    void succeed_Update_Accommodation_ByOwner() {
        // given
        // AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        // Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        // AccommodationRequestDto accommodation2 = AccommodationFactory.mockUpdatedAccommodationRequestDto(city);

        

        // when
        Accommodation updatedAccommodation
                = accommodationService.updateAccommodation(
                savedAccommodation.getId(), accommodation2, authUser);

        // then

        Accommodation findAccommodation = accommodationService.getAccommodationById(updatedAccommodation.getId());
        assertThat(accommodation.getAccommodationName())
                .isNotEqualTo(findAccommodation.getAccommodationName());
    }

    @DisplayName("존재하지 않는 숙소 정보를 수정한다.")
    @Test
    void updateNotExistAccommodation_Success() {
        // given
        AccommodationRequestDto accommodationRequestDto = AccommodationFactory.mockAccommodationRequestDto(city);

        // then
        assertThrows(NotExistAccommodationException.class,
                () -> accommodationService.updateAccommodation(2L, accommodationRequestDto, authUser));
    }

    @DisplayName("숙소 관리자가 아닌 유저가 숙소 정보를 수정할 경우 예외가 발생한다.")
    @Test
    void updateAccommodationByNotOwner_Exception() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);

        AccommodationRequestDto accommodation2 = AccommodationFactory.mockUpdatedAccommodationRequestDto(city);
        AuthUser anotherAuthUser = AuthUser.createInstance(2L, "test2@gmail.com", Role.OWNER);

        // then
        assertThrows(ForbiddenException.class,
                () -> accommodationService.updateAccommodation(
                        savedAccommodation.getId(), accommodation2, anotherAuthUser));
    }

    @DisplayName("숙소 정보를 삭제한다.")
    @Test
    void delete_Success() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);

        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        // when
        accommodationService.deleteAccommodation(accommodationId, authUser);

        // then
        assertThrows(NotExistAccommodationException.class,
                () -> accommodationService.getAccommodationById(accommodationId));
    }

    @DisplayName("숙소 관리자가 아닌 유저가 숙소를 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteAccommodationByNotOwner_Exception() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);

        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        AuthUser anotherAuthUser = AuthUser.createInstance(100000L, "test2@gmail.com", Role.OWNER);

        // then
        assertThrows(ForbiddenException.class,
                () -> accommodationService.deleteAccommodation(accommodationId, anotherAuthUser));
    }

    @DisplayName("관리자는 숙소 정보를 수정할 수 있다.")
    @Test
    void updateAccommodationByAdmin_Success() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        AccommodationRequestDto accommodation2 = AccommodationFactory.mockUpdatedAccommodationRequestDto(city);
        AuthUser anotherAuthUser = AuthUser.createInstance(2L, "admin@gmail.com", Role.ADMIN);

        // when
        Accommodation updatedAccommodation
                = accommodationService.updateAccommodation(accommodationId, accommodation2, anotherAuthUser);

        // then
        Accommodation findAccommodation
                = accommodationService.getAccommodationById(updatedAccommodation.getId());
        Assertions.assertThat(findAccommodation.getAccommodationName())
                .isEqualTo(accommodation2.getAccommodationName());
    }

    @DisplayName("관리자는 숙소 정보를 삭제할 수 있다.")
    @Test
    void deleteAccommodationByAdmin_Success() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);

        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        AuthUser anotherAuthUser = AuthUser.createInstance(2L, "test2@gmail.com", Role.ADMIN);

        // when
        accommodationService.deleteAccommodation(accommodationId, anotherAuthUser);

        // then
        assertThrows(NotExistAccommodationException.class,
                () -> accommodationService.getAccommodationById(accommodationId));
    }

    /*
    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    JpaStateRepository jpaStateRepository;

    @Autowired
    JpaCityRepository jpaCityRepository;

    private String email;
    private String name;
    private String phone;
    private String password;

    private UserCreateRequestDto userCreateRequestDto;
    private User user;
    private AuthUser authUser;

    private Category category;
    private State state;
    private City city;

    @BeforeEach
    public void init() {
        email = "test1223@test";
        name = "testname";
        password = "@Aabcdef";
        phone = "010-1234-1234";

        userCreateRequestDto = new UserCreateRequestDto(email, name, password, phone);
        user = userService.signUp(userCreateRequestDto);
        authUser = AuthUser.createInstance(user.getId(), user.getEmail(), Role.OWNER);

        category = new Category(null, "강릉/경포");
        jpaCategoryRepository.save(category);
        state = new State(null, "강원");
        jpaStateRepository.save(state);
        city = new City(null, "강릉시", state, category.getId());
        jpaCityRepository.save(city);
    }

    @DisplayName("정상적으로 숙소 등록 시 id가 생성된다.")
    @Test
    void saveAccommodation_Success() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        // when
        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        // then
        assertThat(accommodationId)
                .isEqualTo(accommodationService.getAccommodationById(accommodationId).getId());
    }

    @DisplayName("일반 사용자가 숙소 등록 시 예외가 발생한다.")
    @Test
    void basicUserSaveAccommodation_Exception() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        // when
        authUser = AuthUser.createInstance(user.getId(), user.getEmail(), Role.BASIC);
        // then
        assertThrows(ForbiddenException.class,
            () -> accommodationService.saveAccommodation(accommodation, authUser));
    }

    @DisplayName("중복된 숙소명이 있을 경우 등록 시 예외가 발생한다.")
    @Test
    void saveAccommodation_Exception() {

        //given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        accommodationService.saveAccommodation(accommodation, authUser);

        // when
        AccommodationRequestDto anotherAccommodation = AccommodationFactory.mockAnotherAccommodationRequestDto(city);

        // then
        assertThrows(DuplicatedAccommodationException.class,
                () -> accommodationService.saveAccommodation(anotherAccommodation, authUser));
    }

    @DisplayName("숙소 정보를 수정한다.")
    @Test
    void updateAccommodation_Success() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        AccommodationRequestDto accommodation2 = AccommodationFactory.mockUpdatedAccommodationRequestDto(city);

        // when
        Accommodation updatedAccommodation
            = accommodationService.updateAccommodation(
                savedAccommodation.getId(), accommodation2, authUser);

        // then

        Accommodation findAccommodation = accommodationService.getAccommodationById(updatedAccommodation.getId());
        assertThat(accommodation.getAccommodationName())
                .isNotEqualTo(findAccommodation.getAccommodationName());
    }

    @DisplayName("존재하지 않는 숙소 정보를 수정한다.")
    @Test
    void updateNotExistAccommodation_Success() {
        // given
        AccommodationRequestDto accommodationRequestDto = AccommodationFactory.mockAccommodationRequestDto(city);

        // then
        assertThrows(NotExistAccommodationException.class,
                () -> accommodationService.updateAccommodation(2L, accommodationRequestDto, authUser));
    }

    @DisplayName("숙소 관리자가 아닌 유저가 숙소 정보를 수정할 경우 예외가 발생한다.")
    @Test
    void updateAccommodationByNotOwner_Exception() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);

        AccommodationRequestDto accommodation2 = AccommodationFactory.mockUpdatedAccommodationRequestDto(city);
        AuthUser anotherAuthUser = AuthUser.createInstance(2L, "test2@gmail.com", Role.OWNER);

        // then
        assertThrows(ForbiddenException.class,
            () -> accommodationService.updateAccommodation(
                savedAccommodation.getId(), accommodation2, anotherAuthUser));
    }

    @DisplayName("숙소 정보를 삭제한다.")
    @Test
    void delete_Success() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);

        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        // when
        accommodationService.deleteAccommodation(accommodationId, authUser);

        // then
        assertThrows(NotExistAccommodationException.class,
                () -> accommodationService.getAccommodationById(accommodationId));
    }

    @DisplayName("숙소 관리자가 아닌 유저가 숙소를 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteAccommodationByNotOwner_Exception() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);

        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        AuthUser anotherAuthUser = AuthUser.createInstance(100000L, "test2@gmail.com", Role.OWNER);

        // then
        assertThrows(ForbiddenException.class,
            () -> accommodationService.deleteAccommodation(accommodationId, anotherAuthUser));
    }

    @DisplayName("관리자는 숙소 정보를 수정할 수 있다.")
    @Test
    void updateAccommodationByAdmin_Success() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);
        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        AccommodationRequestDto accommodation2 = AccommodationFactory.mockUpdatedAccommodationRequestDto(city);
        AuthUser anotherAuthUser = AuthUser.createInstance(2L, "admin@gmail.com", Role.ADMIN);

        // when
        Accommodation updatedAccommodation
            = accommodationService.updateAccommodation(accommodationId, accommodation2, anotherAuthUser);

        // then
        Accommodation findAccommodation
            = accommodationService.getAccommodationById(updatedAccommodation.getId());
        Assertions.assertThat(findAccommodation.getAccommodationName())
            .isEqualTo(accommodation2.getAccommodationName());
    }

    @DisplayName("관리자는 숙소 정보를 삭제할 수 있다.")
    @Test
    void deleteAccommodationByAdmin_Success() {
        // given
        AccommodationRequestDto accommodation = AccommodationFactory.mockAccommodationRequestDto(city);

        Accommodation savedAccommodation = accommodationService.saveAccommodation(accommodation, authUser);
        Long accommodationId = savedAccommodation.getId();

        AuthUser anotherAuthUser = AuthUser.createInstance(2L, "test2@gmail.com", Role.ADMIN);

        // when
        accommodationService.deleteAccommodation(accommodationId, anotherAuthUser);

        // then
        assertThrows(NotExistAccommodationException.class,
            () -> accommodationService.getAccommodationById(accommodationId));
    }

     */
}
