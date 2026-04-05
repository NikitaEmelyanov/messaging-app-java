package com.telegram.clone.client.network.listener;

import com.telegram.clone.client.model.ChatMessage;

/**
 * Слушатель сетевых событий.
 */
public interface NetworkListener {

    /**
     * Вызывается при успешном подключении.
     */
    void onConnected();

    /**
     * Вызывается при отключении.
     */
    void onDisconnected();

    /**
     * Вызывается при получении сообщения.
     *
     * @param message полученное сообщение
     */
    void onMessageReceived(ChatMessage message);

    /**
     * Вызывается при отправке сообщения.
     *
     * @param message отправленное сообщение
     */
    void onMessageSent(ChatMessage message);

    /**
     * Вызывается при получении системного сообщения.
     *
     * @param message текст системного сообщения
     */
    void onSystemMessage(String message);

    /**
     * Вызывается при ошибке.
     *
     * @param error сообщение об ошибке
     */
    void onError(String error);

    /**
     * Вызывается при изменении статуса пользователя.
     *
     * @param username имя пользователя
     * @param online онлайн статус
     */
    void onUserStatusChanged(String username, boolean online);

    /**
     * Вызывается при изменении статуса печатания.
     *
     * @param username имя пользователя
     * @param isTyping печатает ли пользователь
     */
    void onTypingStatusChanged(String username, boolean isTyping);
}