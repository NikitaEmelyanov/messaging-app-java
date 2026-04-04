package com.telegram.clone.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление системных команд, доступных пользователям.
 * Определяет поддерживаемые команды для взаимодействия с системой.
 */
public enum CommandType {

    /**
     * Показать список доступных команд
     */
    HELP("/help", "Показать список команд"),

    /**
     * Показать список онлайн пользователей
     */
    USERS("/users", "Показать онлайн пользователей"),

    /**
     * Очистить историю сообщений
     */
    CLEAR("/clear", "Очистить чат"),

    /**
     * Выйти из системы
     */
    LOGOUT("/logout", "Выйти из системы"),

    /**
     * Показать информацию о системе
     */
    INFO("/info", "Информация о системе");

    private final String command;
    private final String description;

    /**
     * Конструктор системной команды
     *
     * @param command строка команды (с префиксом /)
     * @param description описание команды
     */
    CommandType(String command, String description) {
        this.command = command;
        this.description = description;
    }

    /**
     * Получить строку команды
     *
     * @return команда с префиксом /
     */
    public String getCommand() {
        return command;
    }

    /**
     * Получить описание команды
     *
     * @return описание команды
     */
    public String getDescription() {
        return description;
    }

    /**
     * Проверить, является ли строка командой
     *
     * @param input входная строка
     * @return true если строка начинается с /
     */
    public static boolean isCommand(String input) {
        return input != null && input.startsWith("/");
    }

    /**
     * Получить тип команды по строке
     *
     * @param command строка команды
     * @return соответствующий CommandType или null
     */
    public static CommandType fromCommand(String command) {
        for (CommandType cmd : values()) {
            if (cmd.command.equals(command)) {
                return cmd;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return command + " - " + description;
    }
}