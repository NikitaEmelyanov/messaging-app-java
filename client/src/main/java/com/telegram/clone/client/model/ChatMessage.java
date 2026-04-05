package com.telegram.clone.client.model;

import com.telegram.clone.common.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Модель сообщения чата на клиенте.
 * Содержит информацию о сообщении, отправителе, получателе и статусе доставки.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /** Уникальный идентификатор сообщения */
    private String id;

    /** Имя отправителя */
    private String from;

    /** Имя получателя */
    private String to;

    /** Текст сообщения */
    private String content;

    /** Тип сообщения (текст, системное, команда, ошибка) */
    private MessageType type;

    /** Время отправки сообщения */
    private LocalDateTime timestamp;

    /** Флаг, указывающий, что сообщение отправлено текущим пользователем */
    private boolean fromMe;

    /** Текущий статус доставки сообщения */
    private MessageStatus status;

    /**
     * Статусы доставки сообщения.
     */
    public enum MessageStatus {
        SENDING, SENT, DELIVERED, FAILED
    }

    /**
     * Возвращает отформатированное время отправки в формате "HH:mm".
     *
     * @return строка с временем или пустая строка, если время не указано
     */
    public String getFormattedTime() {
        if (timestamp == null) return "";
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Возвращает отображаемое имя отправителя.
     *
     * @return "You" если сообщение отправлено текущим пользователем, иначе имя отправителя
     */
    public String getSenderName() {
        return fromMe ? "You" : from;
    }

    /**
     * Создает модель сообщения из DTO.
     *
     * @param dto DTO сообщения с сервера
     * @param currentUser имя текущего пользователя
     * @return новая модель сообщения
     */
    public static ChatMessage fromDto(com.telegram.clone.common.dto.MessageDto dto, String currentUser) {
        return new ChatMessage(
            dto.id(),
            dto.from(),
            dto.to(),
            dto.content(),
            dto.type(),
            dto.timestamp(),
            currentUser.equals(dto.from()),
            MessageStatus.DELIVERED
        );
    }
}