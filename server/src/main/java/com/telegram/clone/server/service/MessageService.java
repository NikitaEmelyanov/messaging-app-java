package com.telegram.clone.server.service;

import com.telegram.clone.common.dto.MessageDto;
import java.util.Set;

/**
 * Сервис обработки сообщений.
 * Отвечает за маршрутизацию, обработку и рассылку сообщений.
 */
public interface MessageService {

    /**
     * Обработка входящего сообщения.
     *
     * @param message сообщение для обработки
     * @param senderUsername имя отправителя
     */
    void processMessage(MessageDto message, String senderUsername);

    /**
     * Отправить сообщение конкретному пользователю.
     *
     * @param message сообщение для отправки
     * @param recipientUsername имя получателя
     * @param senderUsername имя отправителя
     */
    void sendPrivateMessage(MessageDto message, String recipientUsername, String senderUsername);

    /**
     * Отправить сообщение всем пользователям.
     *
     * @param message сообщение для рассылки
     * @param senderUsername имя отправителя
     */
    void broadcastMessage(MessageDto message, String senderUsername);

    /**
     * Оповестить всех об изменении статуса пользователя.
     *
     * @param username имя пользователя
     * @param isOnline новый статус (онлайн/оффлайн)
     */
    void broadcastUserStatus(String username, boolean isOnline);

    /**
     * Получить пользователей, тех кто онлайн
     * @return Множество пользователей, которые находятся в статусе онлайн
     */
    Set<String> getOnlineUsers();

    /**
     * Получить число пользователей, у которых статус Активен
     * @return Количество пользователей со статусом Активен
     */
    int getActiveUsersCount();
}