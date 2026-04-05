package com.telegram.clone.client.service.impl;

import com.telegram.clone.client.model.ChatMessage;
import com.telegram.clone.client.model.ClientUser;
import com.telegram.clone.client.network.ChatWebSocketClient;
import com.telegram.clone.client.network.listener.NetworkListener;
import com.telegram.clone.client.repository.IMessageRepository;
import com.telegram.clone.client.repository.impl.InMemoryMessageRepository;
import com.telegram.clone.client.service.IChatService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса чата.
 * Управляет подключением к серверу, отправкой сообщений и историей.
 */
@Slf4j
public class ChatService implements IChatService {

    private ChatWebSocketClient webSocketClient;
    private IMessageRepository messageRepository;
    private ClientUser currentUser;
    private NetworkListener networkListener;

    /** Конструктор сервиса чата. */
    public ChatService() {
        this.messageRepository = new InMemoryMessageRepository();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(ClientUser user, NetworkListener listener) {
        this.currentUser = user;
        this.networkListener = listener;
        this.webSocketClient = new ChatWebSocketClient(user.getToken(), listener);
        webSocketClient.connectClient();
        log.info("Chat service connected for user: {}", user.getUsername());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.disconnectClient();
        }
        log.info("Chat service disconnected");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatMessage sendMessage(String recipient, String content) {
        ChatMessage message = new ChatMessage(
            UUID.randomUUID().toString(),
            currentUser.getUsername(),
            recipient,
            content,
            null,
            LocalDateTime.now(),
            true,
            ChatMessage.MessageStatus.SENDING
        );

        messageRepository.saveMessage(recipient, message);

        if (webSocketClient != null && webSocketClient.isConnected()) {
            boolean sent = webSocketClient.sendChatMessage(recipient, content);
            message.setStatus(sent ? ChatMessage.MessageStatus.SENT : ChatMessage.MessageStatus.FAILED);

            if (networkListener != null && sent) {
                networkListener.onMessageSent(message);
            } else if (!sent && networkListener != null) {
                networkListener.onError("Failed to send message");
            }
        } else {
            message.setStatus(ChatMessage.MessageStatus.FAILED);
            log.warn("Cannot send message: not connected to server");
        }

        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatMessage> getMessageHistory(String username) {
        return messageRepository.getMessages(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypingStatus(String recipient, boolean isTyping) {
        if (webSocketClient != null && webSocketClient.isConnected()) {
            webSocketClient.sendTypingStatus(recipient, isTyping);
        }
    }

    /**
     * Возвращает текущего пользователя.
     *
     * @return текущий пользователь
     */
    public ClientUser getCurrentUser() {
        return currentUser;
    }
}