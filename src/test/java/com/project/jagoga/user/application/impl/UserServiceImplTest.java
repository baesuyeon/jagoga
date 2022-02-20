package com.project.jagoga.user.application.impl;

import com.project.jagoga.exception.user.DuplicatedUserException;
import com.project.jagoga.exception.user.ForbiddenException;
import com.project.jagoga.exception.user.NotFoundUserException;
import com.project.jagoga.user.domain.*;
import com.project.jagoga.user.infrastructure.BCryptPasswordEncoder;
import com.project.jagoga.user.presentation.dto.request.UserCreateRequestDto;
import com.project.jagoga.user.presentation.dto.request.UserUpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    private static String email = "test1223@test";

    public static UserCreateRequestDto createUserCreateRequestDto() {
        String name = "testname";
        String password = "@Aabcdef";
        String phone = "010-1234-1234";
        return new UserCreateRequestDto(email, name, password, phone);
    }

    public static UserUpdateRequestDto createUserUpdateRequestDto() {
        String name = "updatename";
        String password = "@Fabcde";
        String phone = "010-4321-4321";
        return new UserUpdateRequestDto(name, password, phone);
    }

    public static AuthUser createBasicAuthUser() {
        return AuthUser.createInstance(1L, email, Role.BASIC);
    }

    public static AuthUser createAdminAuthUser() {
        return AuthUser.createInstance(2L, email, Role.ADMIN);
    }

    @Test
    @DisplayName("정상 회원가입 테스트")
    public void succeed_Signup() {
        // given
        UserCreateRequestDto dto = createUserCreateRequestDto();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        // when
         userService.signUp(dto);

        // then
        verify(passwordEncoder, times(1)).encrypt(dto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("중복 회원가입 테스트")
    public void should_Fail_Signup_ifDuplicatedEmailUser() {
        // given
        UserCreateRequestDto dto = createUserCreateRequestDto();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        Exception exception =
                // then
                assertThrows(DuplicatedUserException.class,
                        // when
                        () -> userService.signUp(dto));

        // then
        assertEquals("이미 존재하는 회원입니다", exception.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 정보를 수정하는 경우 예외발생")
    public void should_Fail_updateUser_ifNotExistUser() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        Exception exception = assertThrows(NotFoundUserException.class,
                // then
                () -> userService.updateUser(1L, createUserUpdateRequestDto(), createBasicAuthUser()));

        assertEquals("사용자를 찾을 수 없습니다", exception.getMessage());
    }

    @Test
    @DisplayName("일반 사용자가 타인의 회원정보 수정하는 경우 예외발생")
    public void should_Fail_UpdateUser_ifUpdateOtherUserInfo() {
        // given
        Long otherId = 123L;
        assertThat(createBasicAuthUser().getId()).isNotEqualTo(otherId);

        // then
        Exception exception = assertThrows(ForbiddenException.class,
                // when
                () -> userService.updateUser(otherId, createUserUpdateRequestDto(), createBasicAuthUser()));

        // then
        assertEquals("권한이 없는 사용자입니다", exception.getMessage());
    }

    @Test
    @DisplayName("관리자가 타인의 회원정보 수정")
    public void succeed_UpdateOtherUser_ByAdmin() {
        // given
        Long otherId = 1L;
        assertThat(createAdminAuthUser().getId()).isNotEqualTo(otherId);

        User foundUser = createUserCreateRequestDto().toEntity();

        String beforeEmail = foundUser.getEmail();
        Role beforeRole = foundUser.getRole();
        String beforeName = foundUser.getName();
        String beforePw = foundUser.getPassword();
        String beforePhone = foundUser.getPhone();

        when(userRepository.findById(otherId))
                .thenReturn(Optional.of(foundUser));

        // when
        User updatedUser = userService.updateUser(otherId, createUserUpdateRequestDto(), createAdminAuthUser());

        // then
        assertThat(beforeEmail).isEqualTo(updatedUser.getEmail());
        assertThat(beforeRole).isEqualTo(updatedUser.getRole());
        assertThat(beforeName).isNotEqualTo(updatedUser.getName());
        assertThat(passwordEncoder.encrypt(beforePw))
                .isNotEqualTo(passwordEncoder.encrypt(updatedUser.getPassword()));
        assertThat(beforePhone).isNotEqualTo(updatedUser.getPhone());
    }

    @Test
    @DisplayName("일반 사용자가 본인의 회원정보 수정")
    public void succeed_UpdateUser_ByMyself() {
        // given
        AuthUser basicAuthUser = createBasicAuthUser();

        User foundUser = createUserCreateRequestDto().toEntity();

        when(userRepository.findById(basicAuthUser.getId()))
                .thenReturn(Optional.of(foundUser));

        String beforeEmail = foundUser.getEmail();
        Role beforeRole = foundUser.getRole();
        String beforeName = foundUser.getName();
        String beforePw = foundUser.getPassword();
        String beforePhone = foundUser.getPhone();

        // when
        User updatedUser = userService.updateUser(basicAuthUser.getId(), createUserUpdateRequestDto(), basicAuthUser);

        // then
        assertThat(beforeEmail).isEqualTo(updatedUser.getEmail());
        assertThat(beforeRole).isEqualTo(updatedUser.getRole());
        assertThat(beforeName).isNotEqualTo(updatedUser.getName());
        assertThat(passwordEncoder.encrypt(beforePw))
                .isNotEqualTo(passwordEncoder.encrypt(updatedUser.getPassword()));
        assertThat(beforePhone).isNotEqualTo(updatedUser.getPhone());
    }
}