package com.telegram.clone.server.handler;

import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.common.enums.MessageType;
import com.telegram.clone.common.exception.ChatException;
import com.telegram.clone.server.service.impl.UserSessionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Обработчик аутентификационных сообщений.
 * Проверяет, авторизован ли отправитель.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthMessageHandler implements MessageHandler {

    private final UserSessionServiceImpl sessionService;
    private MessageHandler nextHandler;

    /**
     * Обработать сообщение - проверить авторизацию.
     */
    @Override
    public boolean handle(MessageDto message, String senderUsername) {
        log.debug("AuthMessageHandler processing message from: {}", senderUsername);

        // Проверка авторизации отправителя
        if (senderUsername == null || !sessionService.isUserOnline(senderUsername)) {
            log.warn("Unauthorized message attempt from: {}", senderUsername);
            throw new ChatException.AuthenticationException(
                "User not authenticated or session expired"
            );
        }

        // Проверка для системных сообщений
        if (message.type() == MessageType.SYSTEM || message.type() == MessageType.ERROR) {
            if (!"system".equals(message.from())) {
                log.warn("Attempt to send system message from non-system user: {}", senderUsername);
                throw new ChatException.AuthenticationException(
                    "Not authorized to send system messages"
                );
            }
        }

        // Передача следующему обработчику
        if (nextHandler != null) {
            return nextHandler.handle(message, senderUsername);
        }

        return true;
    }

    /**
     * Установить следующий обработчик.
     */
    @Override
    public MessageHandler setNext(MessageHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }
}