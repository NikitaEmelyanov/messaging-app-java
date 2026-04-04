package com.telegram.clone.server.exception;

import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.common.exception.ChatException;
import com.telegram.clone.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Глобальный обработчик исключений для WebSocket.
 * Централизованно обрабатывает все исключения, возникающие при WebSocket коммуникации.
 */
@Slf4j
@Component
public class GlobalExceptionHandler {

    /**
     * Обработать исключение в WebSocket сессии.
     *
     * @param session WebSocket сессия
     * @param exception возникшее исключение
     */
    public void handleWebSocketError(WebSocketSession session, Throwable exception) {
        String sessionId = session != null ? session.getId() : "unknown";
        log.error("WebSocket error for session {}: {}", sessionId, exception.getMessage(), exception);

        if (session != null && session.isOpen()) {
            try {
                MessageDto errorMessage = createErrorMessage(exception);
                String jsonError = JsonUtils.toJson(errorMessage);
                session.sendMessage(new org.springframework.web.socket.TextMessage(jsonError));

                // Закрываем сессию при критических ошибках
                if (isCriticalError(exception)) {
                    session.close(CloseStatus.SERVER_ERROR);
                }
            } catch (IOException e) {
                log.error("Failed to send error message to session {}", sessionId, e);
            }
        }
    }

    /**
     * Создать сообщение об ошибке.
     *
     * @param exception исключение
     * @return DTO сообщения об ошибке
     */
    private MessageDto createErrorMessage(Throwable exception) {
        String errorMessage;

        if (exception instanceof ChatException chatException) {
            errorMessage = getErrorMessageByCode(chatException.getErrorCode());
            if (errorMessage == null) {
                errorMessage = chatException.getMessage();
            }
        } else {
            errorMessage = "Internal server error. Please try again later.";
        }

        return MessageDto.error(errorMessage);
    }

    /**
     * Получить сообщение по коду ошибки.
     */
    private String getErrorMessageByCode(String errorCode) {
        if (errorCode == null) return null;

        return switch (errorCode) {
            case "AUTH_001", "AUTH_002" -> "Authentication failed. Please login again.";
            case "USER_001" -> "User not found.";
            case "VALID_001", "VALID_002" -> "Invalid message format.";
            case "NET_001", "NET_002" -> "Network error. Please check your connection.";
            case "MSG_001", "MSG_002" -> "Failed to send message. Please try again.";
            default -> null;
        };
    }

    /**
     * Проверить, является ли ошибка критической.
     */
    private boolean isCriticalError(Throwable exception) {
        return exception instanceof ChatException.AuthenticationException ||
               exception instanceof ChatException.ValidationException;
    }
}