package com.telegram.clone.server.service.impl;

import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.common.enums.MessageType;
import com.telegram.clone.common.exception.ChatException;
import com.telegram.clone.server.broker.MessageBroker;
import com.telegram.clone.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Реализация сервиса обработки сообщений.
 * Использует MessageBroker для рассылки сообщений.
 *
 * @author Telegram Clone Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageBroker messageBroker;
    private final UserSessionServiceImpl sessionService;

    /**
     * Обработка входящего сообщения.
     */
    @Override
    @Async("messageExecutor")
    public void processMessage(MessageDto message, String senderUsername) {
        log.debug("Processing message from {}: {}", senderUsername, message);

        // Валидация сообщения
        validateMessage(message, senderUsername);

        // Обогащение сообщения
        MessageDto enrichedMessage = enrichMessage(message, senderUsername);

        // Маршрутизация сообщения
        if (enrichedMessage.isPrivate() && enrichedMessage.to() != null) {
            sendPrivateMessage(enrichedMessage, enrichedMessage.to(), senderUsername);
        } else {
            broadcastMessage(enrichedMessage, senderUsername);
        }
    }

    /**
     * Отправить сообщение конкретному пользователю.
     */
    @Override
    @Async("messageExecutor")
    public void sendPrivateMessage(MessageDto message, String recipientUsername, String senderUsername) {
        log.debug("Sending private message from {} to {}", senderUsername, recipientUsername);

        // Проверка существования получателя
        if (!messageBroker.isUserOnline(recipientUsername)) {
            log.warn("Recipient {} is offline", recipientUsername);
            sendOfflineNotification(senderUsername, recipientUsername);
            return;
        }

        // Отправка сообщения получателю через брокер
        boolean sent = messageBroker.sendToUser(recipientUsername, message);

        if (sent) {
            log.debug("Private message sent from {} to {}", senderUsername, recipientUsername);

            // Отправка подтверждения отправителю
            MessageDto confirmation = MessageDto.system("Message delivered to " + recipientUsername);
            messageBroker.sendToUser(senderUsername, confirmation);
        } else {
            log.error("Failed to send private message from {} to {}", senderUsername, recipientUsername);
            throw new ChatException.MessageSendException(
                "Failed to send message to " + recipientUsername
            );
        }
    }

    /**
     * Отправить сообщение всем пользователям.
     */
    @Override
    @Async("messageExecutor")
    public void broadcastMessage(MessageDto message, String senderUsername) {
        log.debug("Broadcasting message from {} to all users", senderUsername);

        int sentCount = messageBroker.broadcastToAll(message, senderUsername);
        log.debug("Message broadcast to {} users", sentCount);

        if (sentCount == 0 && messageBroker.getActiveConnectionsCount() > 0) {
            log.warn("Broadcast failed: no messages were sent");
        }
    }

    /**
     * Оповестить всех об изменении статуса пользователя.
     */
    @Override
    @Async("connectionExecutor")
    public void broadcastUserStatus(String username, boolean isOnline) {
        log.info("Broadcasting user status: {} is {}", username, isOnline ? "online" : "offline");

        String statusText = isOnline ? "joined the chat" : "left the chat";
        MessageDto statusMessage = MessageDto.system(String.format("%s %s", username, statusText));

        int sentCount = messageBroker.broadcastToAll(statusMessage, username);
        log.debug("User status broadcast to {} users", sentCount);
    }

    /**
     * Получить список онлайн пользователей.
     */
    public Set<String> getOnlineUsers() {
        return sessionService.getOnlineUsers();
    }

    /**
     * Получить количество активных пользователей.
     */
    public int getActiveUsersCount() {
        return sessionService.getActiveUsersCount();
    }

    /**
     * Валидация сообщения.
     */
    private void validateMessage(MessageDto message, String senderUsername) {
        if (message == null) {
            throw new ChatException.ValidationException("Message cannot be null");
        }

        if (message.content() == null || message.content().trim().isEmpty()) {
            throw new ChatException.ValidationException("Message content cannot be empty");
        }

        if (message.content().length() > 5000) {
            throw new ChatException.ValidationException("Message too long (max 5000 characters)");
        }

        if (message.from() != null && !message.from().equals(senderUsername)) {
            throw new ChatException.ValidationException("Sender mismatch");
        }
    }

    /**
     * Обогащение сообщения метаданными.
     */
    private MessageDto enrichMessage(MessageDto message, String senderUsername) {
        return new MessageDto(
            message.id(),
            senderUsername,
            message.to(),
            message.content().trim(),
            message.type() != null ? message.type() : MessageType.TEXT,
            LocalDateTime.now(),
            message.isPrivate()
        );
    }

    /**
     * Отправить уведомление об оффлайн получателе.
     */
    private void sendOfflineNotification(String senderUsername, String recipientUsername) {
        MessageDto notification = MessageDto.system(
            String.format("User %s is offline. Message will be delivered when they come online.",
                recipientUsername)
        );
        messageBroker.sendToUser(senderUsername, notification);
    }
}