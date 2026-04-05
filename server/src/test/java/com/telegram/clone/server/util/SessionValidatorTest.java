package com.telegram.clone.server.util;

import com.telegram.clone.server.model.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SessionValidatorTest {

    private SessionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SessionValidator();
    }

    @Test
    @DisplayName("Должен валидировать корректный формат токена")
    void testValidTokenFormat() {
        assertThat(validator.isValidTokenFormat("abc123-456def-789-12345")).isTrue();
    }

    @Test
    @DisplayName("Должен отклонить некорректный формат токена")
    void testInvalidTokenFormat() {
        assertThat(validator.isValidTokenFormat("invalid!@#")).isFalse();
        assertThat(validator.isValidTokenFormat(null)).isFalse();
        assertThat(validator.isValidTokenFormat("")).isFalse();
        assertThat(validator.isValidTokenFormat("   ")).isFalse();
    }

    @Test
    @DisplayName("Должен определить активную сессию")
    void testIsSessionActive() {
        UserSession session = UserSession.builder()
            .token("token123")
            .username("alice")
            .active(true)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .lastActivityAt(LocalDateTime.now())
            .build();

        assertThat(validator.isSessionActive(session)).isTrue();
    }

    @Test
    @DisplayName("Должен определить неактивную сессию")
    void testInactiveSession() {
        UserSession session = UserSession.builder()
            .token("token123")
            .username("alice")
            .active(false)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .build();

        assertThat(validator.isSessionActive(session)).isFalse();
    }

    @Test
    @DisplayName("Должен определить просроченную сессию")
    void testExpiredSession() {
        UserSession session = UserSession.builder()
            .token("token123")
            .username("alice")
            .active(true)
            .expiresAt(LocalDateTime.now().minusHours(1))
            .build();

        assertThat(validator.isExpired(session)).isTrue();
        assertThat(validator.isSessionActive(session)).isFalse();
    }

    @Test
    @DisplayName("Должен вернуть валидный результат для корректной сессии")
    void testValidateValidSession() {
        UserSession session = UserSession.builder()
            .token("token123")
            .username("alice")
            .active(true)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .lastActivityAt(LocalDateTime.now())
            .build();

        var result = validator.validate(session);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getReason()).isNull();
    }

    @Test
    @DisplayName("Должен вернуть невалидный результат для null сессии")
    void testValidateNullSession() {
        var result = validator.validate(null);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReason()).contains("null");
    }

    @Test
    @DisplayName("Должен вернуть невалидный результат для неактивной сессии")
    void testValidateInactiveSession() {
        UserSession session = UserSession.builder()
            .token("token123")
            .username("alice")
            .active(false)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .build();

        var result = validator.validate(session);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReason()).contains("inactive");
    }
}