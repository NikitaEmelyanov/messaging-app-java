package com.telegram.clone.client.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.clone.client.model.ChatMessage;
import com.telegram.clone.client.network.listener.NetworkListener;
import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.common.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WebSocket клиент для связи с сервером чата.
 * Обеспечивает двустороннюю связь в реальном времени.
 */
@Slf4j
public class ChatWebSocketClient extends WebSocketClient {

    private final String token;
    private final NetworkListener listener;
    private final ObjectMapper objectMapper;
    private boolean connected;

    /**
     * Конструктор WebSocket клиента.
     *
     * @param token токен аутентификации
     * @param listener слушатель сетевых событий
     */
    public ChatWebSocketClient(String token, NetworkListener listener) {
        super(URI.create("ws://localhost:8080/ws/chat?token=" + token));
        this.token = token;
        this.listener = listener;
        this.objectMapper = new ObjectMapper();
        this.connected = false;
    }

    /** Устанавливает соединение с сервером. */
    public void connectClient() {
        try {
            connectBlocking();
            log.info("WebSocket client connected");
        } catch (InterruptedException e) {
            log.error("Failed to connect WebSocket client", e);
            Thread.currentThread().interrupt();
        }
    }

    /** Закрывает соединение с сервером. */
    public void disconnectClient() {
        try {
            closeBlocking();
            log.info("WebSocket client disconnected");
        } catch (InterruptedException e) {
            log.error("Failed to disconnect WebSocket client", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Проверяет, установлено ли соединение.
     *
     * @return true если соединение активно
     */
    public boolean isConnected() {
        return connected && isOpen();
    }

    /**
     * Отправляет текстовое сообщение.
     *
     * @param recipient имя получателя
     * @param content текст сообщения
     * @return true если сообщение отправлено успешно
     */
    public boolean sendChatMessage(String recipient, String content) {
        if (!isConnected()) {
            log.warn("Cannot send message: not connected");
            return false;
        }

        try {
            MessageDto message = new MessageDto(
                UUID.randomUUID().toString(),
                null,
                recipient,
                content,
                MessageType.TEXT,
                LocalDateTime.now(),
                recipient != null && !recipient.isEmpty()
            );

            String json = objectMapper.writeValueAsString(message);
            send(json);
            log.debug("Message sent to: {}", recipient);
            return true;
        } catch (Exception e) {
            log.error("Failed to send message", e);
            return false;
        }
    }

    /**
     * Отправляет статус печатания сообщения.
     *
     * @param recipient имя получателя
     * @param isTyping флаг печатания
     */
    public void sendTypingStatus(String recipient, boolean isTyping) {
        if (!isConnected()) {
            return;
        }

        try {
            MessageDto message = new MessageDto(
                UUID.randomUUID().toString(),
                null,
                recipient,
                isTyping ? "typing" : "stopped_typing",
                MessageType.COMMAND,
                LocalDateTime.now(),
                true
            );

            String json = objectMapper.writeValueAsString(message);
            send(json);
        } catch (Exception e) {
            log.error("Failed to send typing status", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onOpen(ServerHandshake handshake) {
        connected = true;
        log.info("WebSocket connection opened");
        if (listener != null) {
            listener.onConnected();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onMessage(String message) {
        try {
            MessageDto dto = objectMapper.readValue(message, MessageDto.class);

            if (dto.type() == MessageType.SYSTEM) {
                log.info("System message: {}", dto.content());
                if (listener != null) {
                    listener.onSystemMessage(dto.content());
                }
            } else if (dto.type() == MessageType.ERROR) {
                log.error("Error message: {}", dto.content());
                if (listener != null) {
                    listener.onError(dto.content());
                }
            } else {
                ChatMessage chatMessage = new ChatMessage(
                    dto.id(),
                    dto.from(),
                    dto.to(),
                    dto.content(),
                    dto.type(),
                    dto.timestamp(),
                    false,
                    ChatMessage.MessageStatus.DELIVERED
                );

                if (listener != null) {
                    listener.onMessageReceived(chatMessage);
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse message: {}", message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        connected = false;
        log.info("WebSocket connection closed: {} - {}", code, reason);
        if (listener != null) {
            listener.onDisconnected();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onError(Exception ex) {
        log.error("WebSocket error", ex);
        if (listener != null) {
            listener.onError(ex.getMessage());
        }
    }
}