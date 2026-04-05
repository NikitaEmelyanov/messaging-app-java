package com.telegram.clone.server.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO ответа на отправку сообщения.
 *
 * @param success флаг успешности
 * @param messageId идентификатор сообщения
 * @param timestamp время отправки
 * @param errorMessage сообщение об ошибке (при неудаче)
 */
@Schema(description = "Ответ на отправку сообщения")
public record MessageResponse(

    @Schema(description = "Флаг успешной отправки",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED)
    boolean success,

    @Schema(description = "Уникальный идентификатор сообщения",
        example = "msg-12345678-1234-1234-1234-123456789012")
    String messageId,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Время отправки сообщения",
        example = "2024-01-15 14:30:00")
    LocalDateTime timestamp,

    @Schema(description = "Сообщение об ошибке (при неудаче)",
        example = "Recipient is offline")
    String errorMessage

) {
    /**
     * Создать успешный ответ.
     *
     * @param messageId идентификатор сообщения
     * @return успешный ответ
     */
    public static MessageResponse success(String messageId) {
        return new MessageResponse(true, messageId, LocalDateTime.now(), null);
    }

    /**
     * Создать ответ с ошибкой.
     *
     * @param errorMessage сообщение об ошибке
     * @return ответ с ошибкой
     */
    public static MessageResponse error(String errorMessage) {
        return new MessageResponse(false, null, LocalDateTime.now(), errorMessage);
    }

    /**
     * Создать ответ о том, что получатель оффлайн.
     *
     * @param recipientName имя получателя
     * @return ответ с ошибкой
     */
    public static MessageResponse recipientOffline(String recipientName) {
        return error("User " + recipientName + " is offline");
    }
}