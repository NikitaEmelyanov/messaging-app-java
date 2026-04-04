package com.telegram.clone.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.telegram.clone.common.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO для передачи сообщений между клиентом и сервером.
 * Использует record для неизменяемости и компактности.
 *
 * @param id уникальный идентификатор сообщения
 * @param from имя отправителя
 * @param to имя получателя (опционально)
 * @param content текст сообщения
 * @param type тип сообщения
 * @param timestamp время отправки
 * @param isPrivate флаг личного сообщения
 */
@Schema(description = "DTO для передачи сообщений")
public record MessageDto(

    @Schema(description = "Уникальный идентификатор сообщения",
        example = "msg-12345678-1234-1234-1234-123456789012")
    String id,

    @NotBlank(message = "Отправитель не может быть пустым")
    @Schema(description = "Имя пользователя отправителя",
        example = "alice",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String from,

    @Schema(description = "Имя пользователя получателя (null для всех)",
        example = "bob")
    String to,

    @NotBlank(message = "Содержимое сообщения не может быть пустым")
    @Size(max = 5000, message = "Сообщение не может превышать 5000 символов")
    @Schema(description = "Текст сообщения",
        example = "Hello, Bob!",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 5000)
    String content,

    @NotNull(message = "Тип сообщения обязателен")
    @Schema(description = "Тип сообщения",
        example = "TEXT",
        requiredMode = Schema.RequiredMode.REQUIRED)
    MessageType type,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Время отправки сообщения",
        example = "2024-01-15 14:30:00")
    LocalDateTime timestamp,

    @Schema(description = "Флаг личного сообщения",
        example = "true")
    boolean isPrivate

) {

    /**
     * Компактный конструктор для валидации
     */
    public MessageDto {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (type == null) {
            type = MessageType.TEXT;
        }
    }

    /**
     * Создать DTO из модели ChatMessage
     *
     * @param message модель сообщения
     * @return DTO сообщения
     */
    public static MessageDto fromModel(com.telegram.clone.common.model.ChatMessage message) {
        return new MessageDto(
            message.getId(),
            message.getFrom(),
            message.getTo(),
            message.getContent(),
            message.getType(),
            message.getTimestamp(),
            message.isPrivate()
        );
    }

    /**
     * Преобразовать DTO в модель ChatMessage
     *
     * @return модель сообщения
     */
    public com.telegram.clone.common.model.ChatMessage toModel() {
        return com.telegram.clone.common.model.ChatMessage.builder()
            .id(this.id)
            .from(this.from)
            .to(this.to)
            .content(this.content)
            .type(this.type)
            .timestamp(this.timestamp)
            .build();
    }

    /**
     * Создать системное уведомление
     *
     * @param content текст уведомления
     * @return DTO системного сообщения
     */
    public static MessageDto system(String content) {
        return new MessageDto(
            null,
            "system",
            null,
            content,
            MessageType.SYSTEM,
            LocalDateTime.now(),
            false
        );
    }

    /**
     * Создать сообщение об ошибке
     *
     * @param errorMessage текст ошибки
     * @return DTO сообщения об ошибке
     */
    public static MessageDto error(String errorMessage) {
        return new MessageDto(
            null,
            "system",
            null,
            errorMessage,
            MessageType.ERROR,
            LocalDateTime.now(),
            false
        );
    }
}