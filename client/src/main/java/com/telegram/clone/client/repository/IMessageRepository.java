package com.telegram.clone.client.repository;

import com.telegram.clone.client.model.ChatMessage;

import java.util.List;

/**
 * Репозиторий сообщений для локального хранения.
 */
public interface IMessageRepository {

    /**
     * Сохранение сообщения.
     *
     * @param chatId идентификатор чата (имя собеседника)
     * @param message сообщение
     */
    void saveMessage(String chatId, ChatMessage message);

    /**
     * Получение всех сообщений для чата.
     *
     * @param chatId идентификатор чата
     * @return список сообщений
     */
    List<ChatMessage> getMessages(String chatId);

    /**
     * Получение последнего сообщения для чата.
     *
     * @param chatId идентификатор чата
     * @return последнее сообщение или null
     */
    ChatMessage getLastMessage(String chatId);

    /**
     * Очистка истории чата.
     *
     * @param chatId идентификатор чата
     */
    void clearMessages(String chatId);

    /**
     * Получение всех идентификаторов чатов.
     *
     * @return список идентификаторов чатов
     */
    List<String> getAllChatIds();
}