package com.telegram.clone.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Базовое исключение для чат-системы.
 * Содержит код ошибки и дополнительную информацию.
 */
@Getter
@Schema(description = "Базовое исключение чат-системы")
public class ChatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Код ошибки для идентификации типа исключения
     */
    @Schema(description = "Код ошибки",
        example = "AUTH_001")
    private final String errorCode;

    /**
     * Дополнительные детали ошибки
     */
    @Schema(description = "Детали ошибки")
    private final Object details;

    /**
     * Конструктор с сообщением
     *
     * @param message сообщение об ошибке
     */
    public ChatException(String message) {
        super(message);
        this.errorCode = "GENERIC_001";
        this.details = null;
    }

    /**
     * Конструктор с сообщением и причиной
     *
     * @param message сообщение об ошибке
     * @param cause причина ошибки
     */
    public ChatException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERIC_002";
        this.details = null;
    }

    /**
     * Конструктор с кодом ошибки и сообщением
     *
     * @param errorCode код ошибки
     * @param message сообщение об ошибке
     */
    public ChatException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    /**
     * Конструктор с кодом ошибки, сообщением и деталями
     *
     * @param errorCode код ошибки
     * @param message сообщение об ошибке
     * @param details детали ошибки
     */
    public ChatException(String errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * Конструктор с кодом ошибки, сообщением, причиной и деталями
     *
     * @param errorCode код ошибки
     * @param message сообщение об ошибке
     * @param cause причина ошибки
     * @param details детали ошибки
     */
    public ChatException(String errorCode, String message, Throwable cause, Object details) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * Исключение для ошибок аутентификации
     */
    public static class AuthenticationException extends ChatException {
        public AuthenticationException(String message) {
            super("AUTH_001", message);
        }

        public AuthenticationException(String message, Object details) {
            super("AUTH_002", message, details);
        }
    }

    /**
     * Исключение для ошибок, когда пользователь не найден
     */
    public static class UserNotFoundException extends ChatException {
        public UserNotFoundException(String username) {
            super("USER_001", "Пользователь не найден: " + username);
        }
    }

    /**
     * Исключение для ошибок валидации
     */
    public static class ValidationException extends ChatException {
        public ValidationException(String message) {
            super("VALID_001", message);
        }

        public ValidationException(String message, Object details) {
            super("VALID_002", message, details);
        }
    }

    /**
     * Исключение для ошибок сети
     */
    public static class NetworkException extends ChatException {
        public NetworkException(String message) {
            super("NET_001", message);
        }

        public NetworkException(String message, Throwable cause) {
            super("NET_002", message, cause);
        }
    }

    /**
     * Исключение для ошибок отправки сообщения
     */
    public static class MessageSendException extends ChatException {
        public MessageSendException(String message) {
            super("MSG_001", message);
        }

        public MessageSendException(String message, Throwable cause) {
            super("MSG_002", message, cause);
        }
    }

    @Override
    public String toString() {
        return String.format("ChatException{errorCode='%s', message='%s', details=%s}",
            errorCode, getMessage(), details);
    }
}