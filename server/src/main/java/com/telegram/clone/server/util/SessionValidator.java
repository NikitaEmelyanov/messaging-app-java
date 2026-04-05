package com.telegram.clone.server.util;

import com.telegram.clone.server.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Утилитный класс для валидации сессий.
 * Проверяет корректность и актуальность пользовательских сессий.
 */
@Slf4j
@Component
public class SessionValidator {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("^[a-f0-9-]+\\d+[-]?\\d*$");
    private static final int MAX_SESSION_DURATION_HOURS = 72;
    private static final int MAX_IDLE_HOURS = 12;

    /**
     * Проверить валидность токена.
     *
     * @param token токен для проверки
     * @return true если токен имеет корректный формат
     */
    public boolean isValidTokenFormat(String token) {
        if (token == null || token.isBlank()) {
            log.debug("Token is null or blank");
            return false;
        }

        boolean isValid = TOKEN_PATTERN.matcher(token).matches();
        if (!isValid) {
            log.debug("Invalid token format: {}", token);
        }
        return isValid;
    }

    /**
     * Проверить, активна ли сессия.
     *
     * @param session сессия для проверки
     * @return true если сессия активна и не истекла
     */
    public boolean isSessionActive(UserSession session) {
        if (session == null) {
            log.debug("Session is null");
            return false;
        }

        if (!session.isActive()) {
            log.debug("Session is inactive for user: {}", session.getUsername());
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (session.getExpiresAt().isBefore(now)) {
            log.debug("Session expired for user: {}", session.getUsername());
            return false;
        }

        // Проверка на слишком долгую неактивность
        if (session.getLastActivityAt().plusHours(MAX_IDLE_HOURS).isBefore(now)) {
            log.debug("Session idle timeout for user: {}", session.getUsername());
            return false;
        }

        return true;
    }

    /**
     * Проверить, не истекла ли сессия.
     *
     * @param session сессия для проверки
     * @return true если сессия истекла
     */
    public boolean isExpired(UserSession session) {
        if (session == null) return true;

        LocalDateTime now = LocalDateTime.now();
        return session.getExpiresAt().isBefore(now);
    }

    /**
     * Проверить, не превышена ли максимальная длительность сессии.
     *
     * @param session сессия для проверки
     * @return true если длительность превышена
     */
    public boolean isDurationExceeded(UserSession session) {
        if (session == null || session.getCreatedAt() == null) return false;

        LocalDateTime maxExpiry = session.getCreatedAt().plusHours(MAX_SESSION_DURATION_HOURS);
        return LocalDateTime.now().isAfter(maxExpiry);
    }

    /**
     * Валидация сессии с получением причины невалидности.
     *
     * @param session сессия для проверки
     * @return результат валидации
     */
    public ValidationResult validate(UserSession session) {
        if (session == null) {
            return ValidationResult.invalid("Session is null");
        }

        if (!session.isActive()) {
            return ValidationResult.invalid("Session is inactive");
        }

        if (isExpired(session)) {
            return ValidationResult.invalid("Session has expired");
        }

        if (isDurationExceeded(session)) {
            return ValidationResult.invalid("Session duration exceeded maximum");
        }

        if (session.getUsername() == null || session.getUsername().isBlank()) {
            return ValidationResult.invalid("Username is missing in session");
        }

        return ValidationResult.valid();
    }

    /**
     * Результат валидации сессии.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String reason;

        private ValidationResult(boolean valid, String reason) {
            this.valid = valid;
            this.reason = reason;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String reason) {
            return new ValidationResult(false, reason);
        }

        public boolean isValid() {
            return valid;
        }

        public String getReason() {
            return reason;
        }

        @Override
        public String toString() {
            return valid ? "Valid" : "Invalid: " + reason;
        }
    }
}