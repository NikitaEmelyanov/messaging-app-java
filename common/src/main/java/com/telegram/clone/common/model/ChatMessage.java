package com.telegram.clone.common.model;

import com.telegram.clone.common.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель сообщения чата.
 * Представляет собой единицу обмена данными между пользователями.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор сообщения
     */
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    /**
     * Имя пользователя отправителя
     */
    private String from;

    /**
     * Имя пользователя получателя (null для всех)
     */
    private String to;

    /**
     * Текст сообщения
     */
    private String content;

    /**
     * Тип сообщения
     */
    @Builder.Default
    private MessageType type = MessageType.TEXT;

    /**
     * Время отправки сообщения
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Время получения сообщения сервером
     */
    private LocalDateTime receivedAt;

    /**
     * Флаг, указывающий на успешность операции
     */
    @Builder.Default
    private boolean success = true;

    /**
     * Сообщение об ошибке (если есть)
     */
    @Schema(description = "Сообщение об ошибке")
    private String errorMessage;

    /**
     * Создать текстовое сообщение
     *
     * @param from отправитель
     * @param to получатель
     * @param content текст сообщения
     * @return новое сообщение
     */
    public static ChatMessage createTextMessage(String from, String to, String content) {
        return ChatMessage.builder()
            .from(from)
            .to(to)
            .content(content)
            .type(MessageType.TEXT)
            .build();
    }

    /**
     * Создать системное уведомление
     *
     * @param content текст уведомления
     * @return системное сообщение
     */
    public static ChatMessage createSystemMessage(String content) {
        return ChatMessage.builder()
            .from("system")
            .content(content)
            .type(MessageType.SYSTEM)
            .build();
    }

    /**
     * Создать сообщение об ошибке
     *
     * @param errorMessage текст ошибки
     * @return сообщение об ошибке
     */
    public static ChatMessage createErrorMessage(String errorMessage) {
        return ChatMessage.builder()
            .from("system")
            .content(errorMessage)
            .type(MessageType.ERROR)
            .success(false)
            .errorMessage(errorMessage)
            .build();
    }

    /**
     * Проверка, является ли сообщение личным
     *
     * @return true если указан конкретный получатель
     */
    public boolean isPrivate() {
        return to != null && !to.isEmpty() && !to.equals("all");
    }

    /**
     * Проверка, является ли сообщение системным
     *
     * @return true если тип SYSTEM или ERROR
     */
    public boolean isSystemMessage() {
        return type == MessageType.SYSTEM || type == MessageType.ERROR;
    }

    /**
     * Отметить сообщение как полученное
     */
    public void markAsReceived() {
        this.receivedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s: %s", timestamp, from, to != null ? to : "all", content);
    }
}