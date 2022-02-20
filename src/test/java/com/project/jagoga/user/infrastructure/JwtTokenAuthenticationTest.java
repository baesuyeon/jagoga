package com.project.jagoga.user.infrastructure;

import com.project.jagoga.exception.user.UnAuthorizedException;
import com.project.jagoga.user.domain.PasswordEncoder;
import com.project.jagoga.user.domain.User;
import com.project.jagoga.user.domain.UserRepository;
import com.project.jagoga.user.presentation.dto.request.LoginRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenAuthenticationTest {

    UserRepository userRepository = mock(UserRepository.class);

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    JwtTokenAuthentication jwtTokenAuthentication =
            new JwtTokenAuthentication("testSecretKey", userRepository, passwordEncoder);

    static String email = "verifyNormalToken@test";
    static String password = "@Aabcdef";

    public User createFoundUser() {
        String name = "testname";
        String phone = "010-1234-1234";
        return User.createInstance(email, name, passwordEncoder.encrypt(password), phone);
    }

    @Test
    @DisplayName("로그인 이후에 발행 된 정상토큰에 대한 검증")
    public void verify_NormalToken_afterLogin() {
        // given
        when(userRepository.getByEmail(email)).thenReturn(Optional.of(createFoundUser()));

        // when
        String token = jwtTokenAuthentication.login(new LoginRequestDto(email, password));

        // then
        jwtTokenAuthentication.verifyLogin(token);
    }

    @Test
    @DisplayName("비정상 토큰에 대한 검증시 예외발생")
    public void should_Fail_AbnormalToken() {
        // when
        String abnormalToken = "abnormalToken";
        Exception exception = assertThrows(UnAuthorizedException.class,
                () -> jwtTokenAuthentication.verifyLogin(abnormalToken));

        // then
        assertEquals("인증되지 않은 사용자입니다", exception.getMessage());
    }

    @Test
    @DisplayName("null 토큰에 대한 검증시 예외발생")
    public void should_Fail_NullToken() {
        // when
        String nullToken = null;
        Exception exception = assertThrows(UnAuthorizedException.class,
                () -> jwtTokenAuthentication.verifyLogin(nullToken));

        // then
        assertEquals("인증되지 않은 사용자입니다", exception.getMessage());
    }
}