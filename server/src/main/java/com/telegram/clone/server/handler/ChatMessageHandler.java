package com.telegram.clone.server.handler;

import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.common.enums.CommandType;
import com.telegram.clone.common.enums.MessageType;
import com.telegram.clone.common.exception.ChatException;
import com.telegram.clone.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Обработчик чат сообщений.
 * Обрабатывает команды и обычные текстовые сообщения.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageHandler implements MessageHandler {

    private final MessageService messageService;
    private MessageHandler nextHandler;

    /**
     * Обработать сообщение - проверить команды и обработать чат.
     */
    @Override
    public boolean handle(MessageDto message, String senderUsername) {
        log.debug("ChatMessageHandler processing message from: {}", senderUsername);

        String content = message.content();

        // Обработка команд
        if (CommandType.isCommand(content)) {
            handleCommand(message, senderUsername);
            return true; // Команда обработана, дальше не передаем
        }

        // Обычное текстовое сообщение - валидация содержимого
        if (message.type() == null || message.type() == MessageType.TEXT) {
            validateTextMessage(content);
        }

        // Передача следующему обработчику
        if (nextHandler != null) {
            return nextHandler.handle(message, senderUsername);
        }

        return true;
    }

    /**
     * Обработка системных команд.
     *
     * @param message сообщение с командой
     * @param senderUsername имя отправителя
     */
    private void handleCommand(MessageDto message, String senderUsername) {
        String command = message.content();
        CommandType commandType = CommandType.fromCommand(command);

        if (commandType == null) {
            MessageDto errorMsg = MessageDto.error("Unknown command: " + command);
            messageService.sendPrivateMessage(errorMsg, senderUsername, "system");
            return;
        }

        switch (commandType) {
            case HELP -> sendHelp(senderUsername);
            case USERS -> sendOnlineUsers(senderUsername);
            case CLEAR -> sendClearConfirmation(senderUsername);
            case INFO -> sendSystemInfo(senderUsername);
            case LOGOUT -> handleLogout(senderUsername);
        }
    }

    /**
     * Отправить справку пользователю.
     */
    private void sendHelp(String username) {
        StringBuilder help = new StringBuilder("Available commands:\n");
        for (CommandType cmd : CommandType.values()) {
            help.append(cmd.getCommand()).append(" - ").append(cmd.getDescription()).append("\n");
        }
        MessageDto helpMsg = MessageDto.system(help.toString());
        messageService.sendPrivateMessage(helpMsg, username, "system");
    }

    /**
     * Отправить список онлайн пользователей.
     */
    private void sendOnlineUsers(String username) {
        Set<String> onlineUsers = messageService.getOnlineUsers();
        String usersList = onlineUsers.isEmpty() ?
            "No users online" :
            "Online users: " + String.join(", ", onlineUsers);
        MessageDto usersMsg = MessageDto.system(usersList);
        messageService.sendPrivateMessage(usersMsg, username, "system");
    }

    /**
     * Отправить подтверждение очистки чата.
     */
    private void sendClearConfirmation(String username) {
        MessageDto clearMsg = MessageDto.system("Chat cleared locally");
        messageService.sendPrivateMessage(clearMsg, username, "system");
    }

    /**
     * Отправить информацию о системе.
     */
    private void sendSystemInfo(String username) {
        String info = String.format(
            "Telegram Clone Server v1.0\n" +
            "Active users: %d\n" +
            "Uptime: Server running\n" +
            "Max message length: 5000 chars",
            messageService.getActiveUsersCount()
        );
        MessageDto infoMsg = MessageDto.system(info);
        messageService.sendPrivateMessage(infoMsg, username, "system");
    }

    /**
     * Обработать выход пользователя.
     */
    private void handleLogout(String username) {
        log.info("User {} requested logout", username);
        // Логика выхода будет обработана в WebSocket контроллере
        MessageDto logoutMsg = MessageDto.system("Logging out...");
        messageService.sendPrivateMessage(logoutMsg, username, "system");
    }

    /**
     * Валидация текстового сообщения.
     */
    private void validateTextMessage(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ChatException.ValidationException("Message cannot be empty");
        }

        if (content.length() > 5000) {
            throw new ChatException.ValidationException("Message too long (max 5000 characters)");
        }
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