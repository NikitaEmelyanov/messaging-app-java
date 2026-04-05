package com.telegram.clone.server.handler;

import com.telegram.clone.common.dto.MessageDto;

/**
 * Базовый интерфейс для обработчиков сообщений (паттерн Chain of Responsibility).
 * Определяет метод для обработки сообщений и ссылку на следующий обработчик.
 */
public interface MessageHandler {

    /**
     * Обработать сообщение.
     *
     * @param message сообщение для обработки
     * @param senderUsername имя отправителя
     * @return true если сообщение обработано, false если нужно передать дальше
     */
    boolean handle(MessageDto message, String senderUsername);

    /**
     * Установить следующий обработчик в цепи.
     *
     * @param nextHandler следующий обработчик
     * @return следующий обработчик для fluent API
     */
    MessageHandler setNext(MessageHandler nextHandler);
}