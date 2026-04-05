package com.telegram.clone.server.service;

import com.telegram.clone.common.dto.AuthRequestDto;
import com.telegram.clone.common.dto.AuthResponseDto;
import com.telegram.clone.server.repository.impl.InMemoryUserRepository;
import com.telegram.clone.server.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceUnitTest {

    private AuthServiceImpl authService;
    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        authService = new AuthServiceImpl(userRepository);
    }

    @Test
    @DisplayName("Должен успешно аутентифицировать пользователя alice")
    void testAuthenticateAlice() {
        AuthRequestDto request = new AuthRequestDto("alice", "pass123");
        AuthResponseDto response = authService.authenticate(request);

        assertThat(response.success()).isTrue();
        assertThat(response.token()).isNotNull();
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.message()).contains("Добро пожаловать");
    }

    @Test
    @DisplayName("Должен успешно аутентифицировать пользователя bob")
    void testAuthenticateBob() {
        AuthRequestDto request = new AuthRequestDto("bob", "qwerty");
        AuthResponseDto response = authService.authenticate(request);

        assertThat(response.success()).isTrue();
        assertThat(response.username()).isEqualTo("bob");
    }

    @Test
    @DisplayName("Должен отклонить аутентификацию с неверным паролем")
    void testAuthenticateWrongPassword() {
        AuthRequestDto request = new AuthRequestDto("alice", "wrong");
        AuthResponseDto response = authService.authenticate(request);

        assertThat(response.success()).isFalse();
        assertThat(response.token()).isNull();
        // Проверяем, что сообщение содержит информацию об ошибке (на русском)
        assertThat(response.message()).contains("Неверное имя пользователя или пароль");
    }

    @Test
    @DisplayName("Должен отклонить аутентификацию несуществующего пользователя")
    void testAuthenticateNonExistentUser() {
        AuthRequestDto request = new AuthRequestDto("nonexistent", "pass");
        AuthResponseDto response = authService.authenticate(request);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).contains("Неверное имя пользователя или пароль");
    }

    @Test
    @DisplayName("Должен отклонить аутентификацию с пустым именем")
    void testAuthenticateEmptyUsername() {
        AuthRequestDto request = new AuthRequestDto("", "pass123");
        AuthResponseDto response = authService.authenticate(request);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).contains("Неверное имя пользователя или пароль");
    }

    @Test
    @DisplayName("Должен отклонить аутентификацию с пустым паролем")
    void testAuthenticateEmptyPassword() {
        AuthRequestDto request = new AuthRequestDto("alice", "");
        AuthResponseDto response = authService.authenticate(request);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).contains("Неверное имя пользователя или пароль");
    }

    @Test
    @DisplayName("Должен валидировать корректный токен")
    void testValidateValidToken() {
        AuthRequestDto request = new AuthRequestDto("alice", "pass123");
        AuthResponseDto response = authService.authenticate(request);

        boolean isValid = authService.validateToken(response.token());
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Должен инвалидировать некорректный токен")
    void testValidateInvalidToken() {
        boolean isValid = authService.validateToken("invalid-token-123");
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Должен инвалидировать null токен")
    void testValidateNullToken() {
        boolean isValid = authService.validateToken(null);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Должен инвалидировать пустой токен")
    void testValidateEmptyToken() {
        boolean isValid = authService.validateToken("");
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Должен инвалидировать сессию при выходе")
    void testInvalidateSession() {
        AuthRequestDto request = new AuthRequestDto("alice", "pass123");
        AuthResponseDto response = authService.authenticate(request);

        authService.invalidateSession("alice");

        boolean isValid = authService.validateToken(response.token());
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Должен получить имя пользователя по токену")
    void testGetUsernameByToken() {
        AuthRequestDto request = new AuthRequestDto("alice", "pass123");
        AuthResponseDto response = authService.authenticate(request);

        String username = authService.getUsernameByToken(response.token());
        assertThat(username).isEqualTo("alice");
    }

    @Test
    @DisplayName("Должен вернуть null для несуществующего токена")
    void testGetUsernameByInvalidToken() {
        String username = authService.getUsernameByToken("invalid-token");
        assertThat(username).isNull();
    }
}