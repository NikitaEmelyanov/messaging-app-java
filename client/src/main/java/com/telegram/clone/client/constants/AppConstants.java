package com.telegram.clone.client.constants;

/**
 * Константы клиентского приложения.
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Constants class");
    }

    /** URL сервера */
    public static final String SERVER_URL = "http://localhost:8080";

    /** WebSocket URL */
    public static final String WS_URL = "ws://localhost:8080/ws/chat";

    /** Максимальная длина сообщения */
    public static final int MAX_MESSAGE_LENGTH = 5000;

    /** Задержка heartbeat (мс) */
    public static final int HEARTBEAT_INTERVAL_MS = 30000;

    /** Задержка для статуса печатания (мс) */
    public static final int TYPING_TIMEOUT_MS = 2000;

    /** Максимальное количество сообщений в истории */
    public static final int MAX_HISTORY_SIZE = 1000;

    /** Цвет фона (темная тема) */
    public static final String BACKGROUND_COLOR = "#1e1e1e";

    /** Цвет фона панелей */
    public static final String PANEL_BACKGROUND_COLOR = "#2d2d2d";

    /** Цвет акцента (Telegram синий) */
    public static final String ACCENT_COLOR = "#4080ff";

    /** Цвет текста */
    public static final String TEXT_COLOR = "#ffffff";

    /** Цвет вторичного текста */
    public static final String SECONDARY_TEXT_COLOR = "#888888";

    /** Цвет ошибки */
    public static final String ERROR_COLOR = "#ff4444";

    /** Цвет успеха */
    public static final String SUCCESS_COLOR = "#44ff44";
}