package com.telegram.clone.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель чат-сессии.
 * Представляет диалог с другим пользователем и управляет историей сообщений.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    /** Имя собеседника */
    private String withUser;

    /** Список сообщений в чате */
    private List<ChatMessage> messages = new ArrayList<>();

    /** Количество непрочитанных сообщений */
    private int unreadCount = 0;

    /** Последнее сообщение в чате */
    private ChatMessage lastMessage;

    /** Флаг, указывающий, что собеседник печатает сообщение */
    private boolean isTyping = false;

    /**
     * Добавляет сообщение в историю чата.
     * Обновляет последнее сообщение и счетчик непрочитанных.
     *
     * @param message добавляемое сообщение
     */
    public void addMessage(ChatMessage message) {
        messages.add(message);
        this.lastMessage = message;
        if (!message.isFromMe()) {
            unreadCount++;
        }
    }

    /**
     * Очищает счетчик непрочитанных сообщений.
     */

    public void clearUnread() {
        unreadCount = 0;
    }

    /**
     * Возвращает текст последнего сообщения для отображения в списке диалогов.
     * Обрезает длинные сообщения и добавляет префикс "You: " для своих сообщений.
     *
     * @return текст последнего сообщения
     */
    public String getLastMessageText() {
        if (lastMessage == null) {
            return "No messages yet";
        }
        String prefix = lastMessage.isFromMe() ? "You: " : "";
        String content = lastMessage.getContent();
        if (content.length() > 50) {
            content = content.substring(0, 47) + "...";
        }
        return prefix + content;
    }
}