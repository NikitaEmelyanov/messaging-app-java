package com.telegram.clone.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление типов сообщений в системе чата.
 * Определяет формат и назначение каждого сообщения.
 */
public enum MessageType {

    /**
     * Текстовое сообщение от пользователя
     */
    TEXT("text", "Обычное текстовое сообщение"),

    /**
     * Системное уведомление (например, пользователь вошел/вышел)
     */
    SYSTEM("system", "Системное уведомление"),

    /**
     * Команда (например, /help, /status)
     */
    COMMAND("command", "Системная команда"),

    /**
     * Ошибка
     */
    ERROR("error", "Сообщение об ошибке");

    private final String code;
    private final String description;

    /**
     * Конструктор для типа сообщения
     *
     * @param code уникальный код типа сообщения
     * @param description описание типа сообщения
     */
    MessageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Получить код типа сообщения
     *
     * @return строковый код типа
     */
    public String getCode() {
        return code;
    }

    /**
     * Получить описание типа сообщения
     *
     * @return описание типа
     */
    public String getDescription() {
        return description;
    }

    /**
     * Проверить, является ли тип текстовым сообщением.
     *
     * @return true если тип TEXT
     */
    public boolean isText() {
        return this == TEXT;
    }

    /**
     * Проверить, является ли тип системным сообщением.
     *
     * @return true если тип SYSTEM или ERROR
     */
    public boolean isSystem() {
        return this == SYSTEM || this == ERROR;
    }

    /**
     * Получить тип сообщения по коду
     *
     * @param code строковый код типа
     * @return соответствующий MessageType или TEXT по умолчанию
     */
    public static MessageType fromCode(String code) {
        for (MessageType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return TEXT;
    }

    @Override
    public String toString() {
        return code;
    }
}