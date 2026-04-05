package com.telegram.clone.client.service;

import com.telegram.clone.client.model.ChatMessage;

import java.util.List;

/**
 * Сервис обработки сообщений на клиенте.
 */
public interface IMessageService {

    /**
     * Обработка входящего сообщения.
     *
     * @param message полученное сообщение
     */
    void onMessageReceived(ChatMessage message);

    /**
     * Обработка статуса печатания.
     *
     * @param username имя пользователя
     * @param isTyping печатает ли пользователь
     */
    void onTypingStatus(String username, boolean isTyping);

    /**
     * Обработка статуса пользователя.
     *
     * @param username имя пользователя
     * @param online онлайн статус
     */
    void onUserStatus(String username, boolean online);

    /**
     * Получение сообщений для чата.
     *
     * @param username имя собеседника
     * @return список сообщений
     */
    List<ChatMessage> getMessagesForChat(String username);

    /**
     * Добавление слушателя сообщений.
     *
     * @param listener слушатель
     */
    void addMessageListener(MessageListener listener);

    /**
     * Удаление слушателя сообщений.
     *
     * @param listener слушатель
     */
    void removeMessageListener(MessageListener listener);

    /**
     * Слушатель сообщений.
     */
    interface MessageListener {
        void onNewMessage(ChatMessage message);
        void onTypingStatusChanged(String username, boolean isTyping);
        void onUserStatusChanged(String username, boolean online);
    }
}