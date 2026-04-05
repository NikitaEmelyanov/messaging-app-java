package com.telegram.clone.client.service;

import com.telegram.clone.client.model.ChatMessage;
import com.telegram.clone.client.model.ClientUser;
import com.telegram.clone.client.network.listener.NetworkListener;

import java.util.List;

/**
 * Сервис чата.
 * Отвечает за отправку и получение сообщений.
 */
public interface IChatService {

    /**
     * Подключение к серверу.
     *
     * @param user текущий пользователь
     * @param listener слушатель сетевых событий
     */
    void connect(ClientUser user, NetworkListener listener);

    /**
     * Отключение от сервера.
     */
    void disconnect();

    /**
     * Отправка сообщения.
     *
     * @param recipient получатель
     * @param content содержимое
     * @return отправленное сообщение
     */
    ChatMessage sendMessage(String recipient, String content);

    /**
     * Получение истории сообщений с пользователем.
     *
     * @param username имя собеседника
     * @return список сообщений
     */
    List<ChatMessage> getMessageHistory(String username);

    /**
     * Установка статуса печатания.
     *
     * @param recipient получатель
     * @param isTyping печатает ли пользователь
     */
    void setTypingStatus(String recipient, boolean isTyping);
}