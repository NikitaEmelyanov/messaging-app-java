package com.telegram.clone.client.service.impl;

import com.telegram.clone.client.model.ChatMessage;
import com.telegram.clone.client.repository.IMessageRepository;
import com.telegram.clone.client.repository.impl.InMemoryMessageRepository;
import com.telegram.clone.client.service.IMessageService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Реализация сервиса обработки сообщений.
 */
@Slf4j
public class MessageService implements IMessageService {

    private final IMessageRepository messageRepository;
    private final List<MessageListener> listeners;
    private final List<String> typingUsers;

    /**
     * Конструктор сервиса сообщений.
     */
    public MessageService() {
        this.messageRepository = new InMemoryMessageRepository();
        this.listeners = new CopyOnWriteArrayList<>();
        this.typingUsers = new ArrayList<>();
    }

    /**
     * Обработка входящего сообщения.
     *
     * @param message полученное сообщение
     */
    @Override
    public void onMessageReceived(ChatMessage message) {
        log.debug("Message received from: {}", message.getFrom());

        // Сохраняем в репозиторий
        String chatPartner = message.isFromMe() ? message.getTo() : message.getFrom();
        messageRepository.saveMessage(chatPartner, message);

        // Уведомляем слушателей
        listeners.forEach(listener -> listener.onNewMessage(message));
    }

    /**
     * Обработка статуса печатания.
     *
     * @param username имя пользователя
     * @param isTyping печатает ли пользователь
     */
    @Override
    public void onTypingStatus(String username, boolean isTyping) {
        if (isTyping && !typingUsers.contains(username)) {
            typingUsers.add(username);
        } else if (!isTyping) {
            typingUsers.remove(username);
        }

        listeners.forEach(listener -> listener.onTypingStatusChanged(username, isTyping));
    }

    /**
     * Обработка статуса пользователя.
     *
     * @param username имя пользователя
     * @param online онлайн статус
     */
    @Override
    public void onUserStatus(String username, boolean online) {
        log.debug("User status changed: {} -> {}", username, online ? "online" : "offline");
        listeners.forEach(listener -> listener.onUserStatusChanged(username, online));
    }

    /**
     * Получение сообщений для чата.
     *
     * @param username имя собеседника
     * @return список сообщений
     */
    @Override
    public List<ChatMessage> getMessagesForChat(String username) {
        return messageRepository.getMessages(username);
    }

    /**
     * Добавление слушателя сообщений.
     *
     * @param listener слушатель
     */
    @Override
    public void addMessageListener(MessageListener listener) {
        listeners.add(listener);
    }

    /**
     * Удаление слушателя сообщений.
     *
     * @param listener слушатель
     */
    @Override
    public void removeMessageListener(MessageListener listener) {
        listeners.remove(listener);
    }

    /**
     * Проверка, печатает ли пользователь.
     *
     * @param username имя пользователя
     * @return true если печатает
     */
    public boolean isTyping(String username) {
        return typingUsers.contains(username);
    }
}