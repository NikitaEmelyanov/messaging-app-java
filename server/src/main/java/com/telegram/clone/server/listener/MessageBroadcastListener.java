package com.telegram.clone.server.listener;

import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.server.broker.MessageBroker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Слушатель событий для рассылки сообщений.
 * Обрабатывает события, связанные с отправкой сообщений.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageBroadcastListener {

    private final MessageBroker messageBroker;

    /**
     * Обработка события отправки сообщения.
     *
     * @param event событие отправки сообщения
     */
    @EventListener
    public void onMessageSent(MessageSentEvent event) {
        log.debug("Message sent event received: {}", event.getMessage());

        if (event.getMessage().isPrivate()) {
            messageBroker.sendToUser(
                event.getMessage().to(),
                event.getMessage()
            );
        } else {
            messageBroker.broadcastToAll(
                event.getMessage(),
                event.getSenderUsername()
            );
        }
    }

    /**
     * Событие отправки сообщения.
     */
    public static class MessageSentEvent {
        private final MessageDto message;
        private final String senderUsername;

        public MessageSentEvent(MessageDto message, String senderUsername) {
            this.message = message;
            this.senderUsername = senderUsername;
        }

        public MessageDto getMessage() {
            return message;
        }

        public String getSenderUsername() {
            return senderUsername;
        }
    }
}