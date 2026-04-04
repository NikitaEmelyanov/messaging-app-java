package com.telegram.clone.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление статусов пользователя в системе.
 * Определяет текущее состояние пользователя.
 */
public enum UserStatus {

    /**
     * Пользователь онлайн (активен)
     */
    ONLINE("online", "В сети"),

    /**
     * Пользователь оффлайн (отключен)
     */
    OFFLINE("offline", "Не в сети"),

    /**
     * Пользователь печатает сообщение
     */
    TYPING("typing", "Печатает..."),

    /**
     * Пользователь не активен длительное время
     */
    AWAY("away", "Отсутствует");

    private final String code;
    private final String displayName;

    /**
     * Конструктор статуса пользователя
     *
     * @param code уникальный код статуса
     * @param displayName отображаемое имя статуса
     */
    UserStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * Получить код статуса
     *
     * @return строковый код статуса
     */
    public String getCode() {
        return code;
    }

    /**
     * Получить отображаемое имя статуса
     *
     * @return отображаемое имя
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Получить статус по коду
     *
     * @param code строковый код статуса
     * @return соответствующий UserStatus или ONLINE по умолчанию
     */
    public static UserStatus fromCode(String code) {
        for (UserStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ONLINE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}