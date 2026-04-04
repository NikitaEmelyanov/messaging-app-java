package com.telegram.clone.server.controller;

import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.common.exception.ChatException;
import com.telegram.clone.common.utils.JsonUtils;
import com.telegram.clone.server.broker.MessageBroker;
import com.telegram.clone.server.exception.GlobalExceptionHandler;
import com.telegram.clone.server.service.MessageService;
import com.telegram.clone.server.service.impl.UserSessionServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket контроллер для обработки чат сообщений.
 * Использует MessageBroker для управления сессиями и рассылки сообщений.
 *
 * @author Telegram Clone Team
 * @version 1.0
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Chat WebSocket", description = "WebSocket контроллер для обмена сообщениями")
public class ChatWebSocketController extends TextWebSocketHandler {

    private final MessageBroker messageBroker;
    private final MessageService messageService;
    private final UserSessionServiceImpl sessionService;
    private final GlobalExceptionHandler exceptionHandler;

    /**
     * Обработка установки WebSocket соединения.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            // Регистрируем сессию в брокере
            messageBroker.registerSession(username, session);
            sessionService.addSession(username, session.getId());

            log.info("WebSocket connection established for user: {}", username);

            // Оповещаем других пользователей о входе
            messageService.broadcastUserStatus(username, true);
        }
        super.afterConnectionEstablished(session);
    }

    /**
     * Обработка входящего WebSocket сообщения.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        try {
            String payload = message.getPayload();
            log.debug("Received message from {}: {}", username, payload);

            MessageDto messageDto = JsonUtils.fromJson(payload, MessageDto.class);

            // Обрабатываем сообщение через сервис
            messageService.processMessage(messageDto, username);

        } catch (ChatException e) {
            log.error("Error processing message from {}: {}", username, e.getMessage());
            exceptionHandler.handleWebSocketError(session, e);
        } catch (Exception e) {
            log.error("Unexpected error processing message from {}", username, e);
            exceptionHandler.handleWebSocketError(session, e);
        }
    }

    /**
     * Обработка закрытия WebSocket соединения.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            // Удаляем сессию из брокера
            messageBroker.unregisterSession(username);
            sessionService.removeSession(username);

            log.info("WebSocket connection closed for user: {}, status: {}", username, status);

            // Оповещаем других пользователей о выходе
            messageService.broadcastUserStatus(username, false);
        }
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String username = (String) session.getAttributes().get("username");
        log.error("WebSocket transport error for user: {}", username, exception);

        if (username != null) {
            messageBroker.unregisterSession(username);
            sessionService.removeSession(username);
            messageService.broadcastUserStatus(username, false);
        }

        super.handleTransportError(session, exception);
    }
}