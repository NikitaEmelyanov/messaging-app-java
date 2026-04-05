package com.telegram.clone.client.repository.impl;

import com.telegram.clone.client.model.ChatMessage;
import com.telegram.clone.client.repository.IMessageRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory реализация репозитория сообщений.
 */
public class InMemoryMessageRepository implements IMessageRepository {

    private final Map<String, List<ChatMessage>> messagesByChat;

    /**
     * Конструктор репозитория.
     */
    public InMemoryMessageRepository() {
        this.messagesByChat = new ConcurrentHashMap<>();
    }

    /**
     * Сохранение сообщения.
     *
     * @param chatId идентификатор чата
     * @param message сообщение
     */
    @Override
    public void saveMessage(String chatId, ChatMessage message) {
        messagesByChat.computeIfAbsent(chatId, k -> new ArrayList<>()).add(message);
    }

    /**
     * Получение всех сообщений для чата.
     *
     * @param chatId идентификатор чата
     * @return список сообщений (отсортированный по времени)
     */
    @Override
    public List<ChatMessage> getMessages(String chatId) {
        List<ChatMessage> messages = messagesByChat.getOrDefault(chatId, new ArrayList<>());
        messages.sort(Comparator.comparing(ChatMessage::getTimestamp));
        return new ArrayList<>(messages);
    }

    /**
     * Получение последнего сообщения для чата.
     *
     * @param chatId идентификатор чата
     * @return последнее сообщение или null
     */
    @Override
    public ChatMessage getLastMessage(String chatId) {
        List<ChatMessage> messages = messagesByChat.get(chatId);
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    /**
     * Очистка истории чата.
     *
     * @param chatId идентификатор чата
     */
    @Override
    public void clearMessages(String chatId) {
        messagesByChat.remove(chatId);
    }

    /**
     * Получение всех идентификаторов чатов.
     *
     * @return список идентификаторов чатов
     */
    @Override
    public List<String> getAllChatIds() {
        return new ArrayList<>(messagesByChat.keySet());
    }
}