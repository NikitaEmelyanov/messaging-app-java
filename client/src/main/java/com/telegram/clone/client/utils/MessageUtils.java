package com.telegram.clone.client.utils;

import com.telegram.clone.client.model.ChatMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Утилиты для работы с сообщениями.
 */
public final class MessageUtils {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private MessageUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Форматирование времени сообщения.
     *
     * @param timestamp время
     * @return отформатированная строка
     */
    public static String formatTime(LocalDateTime timestamp) {
        if (timestamp == null) return "";

        LocalDateTime now = LocalDateTime.now();
        if (timestamp.toLocalDate().equals(now.toLocalDate())) {
            return timestamp.format(TIME_FORMATTER);
        } else {
            return timestamp.format(DATE_FORMATTER);
        }
    }

    /**
     * Группировка сообщений по датам.
     *
     * @param messages список сообщений
     * @return отформатированная строка с группировкой
     */
    public static String groupMessagesByDate(List<ChatMessage> messages) {
        StringBuilder result = new StringBuilder();
        LocalDateTime lastDate = null;

        for (ChatMessage message : messages) {
            LocalDateTime messageDate = message.getTimestamp().toLocalDate().atStartOfDay();
            if (lastDate == null || !lastDate.equals(messageDate)) {
                if (lastDate != null) {
                    result.append("\n");
                }
                result.append("--- ").append(messageDate.format(DATE_FORMATTER)).append(" ---\n");
                lastDate = messageDate;
            }
            result.append(formatTime(message.getTimestamp()))
                .append(" ")
                .append(message.getSenderName())
                .append(": ")
                .append(message.getContent())
                .append("\n");
        }

        return result.toString();
    }

    /**
     * Проверка, является ли сообщение командой.
     *
     * @param text текст сообщения
     * @return true если это команда
     */
    public static boolean isCommand(String text) {
        return text != null && text.startsWith("/");
    }
}