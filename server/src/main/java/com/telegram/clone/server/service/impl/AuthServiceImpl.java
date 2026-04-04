package com.telegram.clone.server.service.impl;

import com.telegram.clone.common.dto.AuthRequestDto;
import com.telegram.clone.common.dto.AuthResponseDto;
import com.telegram.clone.common.exception.ChatException;
import com.telegram.clone.server.repository.UserRepository;
import com.telegram.clone.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация сервиса аутентификации.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> tokenExpiry = new ConcurrentHashMap<>();

    private static final long TOKEN_VALIDITY_HOURS = 24;

    /**
     * Аутентификация пользователя.
     *
     * @param request запрос с учетными данными
     * @return ответ с результатом аутентификации
     */
    @Override
    public AuthResponseDto authenticate(AuthRequestDto request) {
        log.info("Authentication attempt for user: {}", request.username());

        if (!request.isValid()) {
            log.warn("Invalid authentication request for username: {}", request.username());
            return AuthResponseDto.invalidCredentials();
        }

        // Проверка существования пользователя
        var userOpt = userRepository.findByUsername(request.username());
        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", request.username());
            return AuthResponseDto.invalidCredentials();
        }

        var user = userOpt.get();
        String hashedPassword = hashPassword(request.password());

        // Проверка пароля
        if (!user.getPasswordHash().equals(hashedPassword)) {
            log.warn("Invalid password for user: {}", request.username());
            return AuthResponseDto.invalidCredentials();
        }

        // Генерация токена
        String token = generateToken(request.username());
        tokenStore.put(token, request.username());
        tokenExpiry.put(token, LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS));

        log.info("User authenticated successfully: {}", request.username());

        return AuthResponseDto.success(
            user.getUsername(),
            user.getDisplayName(),
            token
        );
    }

    /**
     * Валидация токена сессии.
     *
     * @param token токен сессии
     * @return true если токен валиден
     */
    @Override
    public boolean validateToken(String token) {
        if (token == null || !tokenStore.containsKey(token)) {
            return false;
        }

        LocalDateTime expiry = tokenExpiry.get(token);
        if (expiry != null && expiry.isBefore(LocalDateTime.now())) {
            tokenStore.remove(token);
            tokenExpiry.remove(token);
            return false;
        }

        return true;
    }

    /**
     * Инвалидация сессии пользователя.
     *
     * @param username имя пользователя
     */
    @Override
    public void invalidateSession(String username) {
        tokenStore.entrySet().removeIf(entry -> entry.getValue().equals(username));
        tokenExpiry.keySet().removeIf(token -> tokenStore.get(token) == null);
        log.info("Session invalidated for user: {}", username);
    }

    /**
     * Получить имя пользователя по токену.
     *
     * @param token токен сессии
     * @return имя пользователя или null
     */
    @Override
    public String getUsernameByToken(String token) {
        if (!validateToken(token)) {
            return null;
        }
        return tokenStore.get(token);
    }

    /**
     * Генерация токена для пользователя.
     *
     * @param username имя пользователя
     * @return сгенерированный токен
     */
    private String generateToken(String username) {
        String raw = username + UUID.randomUUID().toString() + System.currentTimeMillis();
        return hashPassword(raw);
    }

    /**
     * Хэширование пароля с использованием SHA-256.
     *
     * @param password пароль для хэширования
     * @return хэш пароля
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Password hashing failed", e);
            throw new ChatException("AUTH_003", "Password hashing error", e);
        }
    }
}