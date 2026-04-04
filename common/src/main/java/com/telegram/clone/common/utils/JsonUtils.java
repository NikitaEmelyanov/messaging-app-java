package com.telegram.clone.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.telegram.clone.common.exception.ChatException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Утилитный класс для работы с JSON.
 * Предоставляет методы для сериализации и десериализации объектов.
 * Использует Jackson для преобразования объектов в JSON и обратно.
 */
@Slf4j
@Schema(description = "Утилиты для работы с JSON")
public final class JsonUtils {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();

        // Регистрация модуля для работы с Java 8+ (LocalDateTime и др.)
        objectMapper.registerModule(new JavaTimeModule());

        // Настройки сериализации
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Настройки десериализации
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    }

    private JsonUtils() {
        // Приватный конструктор для утилитного класса
        throw new UnsupportedOperationException("Это утилитный класс, его нельзя инстанциировать");
    }

    /**
     * Получить настроенный экземпляр ObjectMapper
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Сериализовать объект в JSON строку
     *
     * @param object объект для сериализации
     * @return JSON строка
     * @throws ChatException если произошла ошибка сериализации
     */
    public static String toJson(Object object) throws ChatException {
        try {
            String json = objectMapper.writeValueAsString(object);
            log.debug("Объект сериализован в JSON: {}", json.length());
            return json;
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации объекта в JSON: {}", e.getMessage(), e);
            throw new ChatException("JSON_001", "Ошибка сериализации в JSON", e);
        }
    }

    /**
     * Десериализовать JSON строку в объект указанного типа
     *
     * @param json JSON строка
     * @param clazz класс целевого объекта
     * @param <T> тип объекта
     * @return десериализованный объект
     * @throws ChatException если произошла ошибка десериализации
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws ChatException {
        try {
            T result = objectMapper.readValue(json, clazz);
            log.debug("JSON десериализован в объект типа: {}", clazz.getSimpleName());
            return result;
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации JSON в объект {}: {}",
                clazz.getSimpleName(), e.getMessage(), e);
            throw new ChatException("JSON_002", "Ошибка десериализации из JSON", e);
        }
    }

    /**
     * Десериализовать JSON строку в объект сложного типа
     *
     * @param json JSON строка
     * @param typeReference ссылка на тип (для коллекций и generic)
     * @param <T> тип объекта
     * @return десериализованный объект
     * @throws ChatException если произошла ошибка десериализации
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) throws ChatException {
        try {
            T result = objectMapper.readValue(json, typeReference);
            log.debug("JSON десериализован в объект типа: {}", typeReference.getType());
            return result;
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации JSON: {}", e.getMessage(), e);
            throw new ChatException("JSON_003", "Ошибка десериализации из JSON", e);
        }
    }

    /**
     * Сериализовать объект в JSON и записать в OutputStream
     *
     * @param outputStream поток для записи
     * @param object объект для сериализации
     * @throws ChatException если произошла ошибка
     */
    public static void writeJson(OutputStream outputStream, Object object) throws ChatException {
        try {
            objectMapper.writeValue(outputStream, object);
            log.debug("Объект записан в OutputStream");
        } catch (IOException e) {
            log.error("Ошибка записи JSON в OutputStream: {}", e.getMessage(), e);
            throw new ChatException("JSON_004", "Ошибка записи JSON", e);
        }
    }

    /**
     * Прочитать JSON из InputStream и десериализовать в объект
     *
     * @param inputStream поток для чтения
     * @param clazz класс целевого объекта
     * @param <T> тип объекта
     * @return десериализованный объект
     * @throws ChatException если произошла ошибка
     */
    public static <T> T readJson(InputStream inputStream, Class<T> clazz) throws ChatException {
        try {
            T result = objectMapper.readValue(inputStream, clazz);
            log.debug("Объект прочитан из InputStream, тип: {}", clazz.getSimpleName());
            return result;
        } catch (IOException e) {
            log.error("Ошибка чтения JSON из InputStream: {}", e.getMessage(), e);
            throw new ChatException("JSON_005", "Ошибка чтения JSON", e);
        }
    }

    /**
     * Преобразовать объект в байтовый массив JSON
     *
     * @param object объект для сериализации
     * @return байтовый массив JSON
     * @throws ChatException если произошла ошибка
     */
    public static byte[] toJsonBytes(Object object) throws ChatException {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(object);
            log.debug("Объект сериализован в JSON байтов: {} bytes", bytes.length);
            return bytes;
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации объекта в байтовый JSON: {}", e.getMessage(), e);
            throw new ChatException("JSON_006", "Ошибка сериализации в байтовый JSON", e);
        }
    }

    /**
     * Десериализовать байтовый массив JSON в объект
     *
     * @param bytes байтовый массив JSON
     * @param clazz класс целевого объекта
     * @param <T> тип объекта
     * @return десериализованный объект
     * @throws ChatException если произошла ошибка
     */
    public static <T> T fromJsonBytes(byte[] bytes, Class<T> clazz) throws ChatException {
        try {
            T result = objectMapper.readValue(bytes, clazz);
            log.debug("Байтовый JSON десериализован в объект типа: {}", clazz.getSimpleName());
            return result;
        } catch (IOException e) {
            log.error("Ошибка десериализации байтового JSON: {}", e.getMessage(), e);
            throw new ChatException("JSON_007", "Ошибка десериализации байтового JSON", e);
        }
    }

    /**
     * Проверить, является ли строка валидным JSON
     *
     * @param json строка для проверки
     * @return true если строка является валидным JSON
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * Форматировать JSON строку с отступами
     *
     * @param json неформатированная JSON строка
     * @return отформатированная JSON строка
     * @throws ChatException если произошла ошибка
     */
    public static String prettyPrint(String json) throws ChatException {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            log.error("Ошибка форматирования JSON: {}", e.getMessage(), e);
            throw new ChatException("JSON_008", "Ошибка форматирования JSON", e);
        }
    }

    /**
     * Создать глубокую копию объекта через JSON сериализацию
     *
     * @param original исходный объект
     * @param clazz класс объекта
     * @param <T> тип объекта
     * @return глубокая копия объекта
     * @throws ChatException если произошла ошибка
     */
    public static <T> T deepCopy(T original, Class<T> clazz) throws ChatException {
        String json = toJson(original);
        return fromJson(json, clazz);
    }

    /**
     * Обновить существующий объект данными из JSON
     *
     * @param json JSON строка с новыми данными
     * @param target целевой объект для обновления
     * @throws ChatException если произошла ошибка
     */
    public static void updateObject(String json, Object target) throws ChatException {
        try {
            objectMapper.readerForUpdating(target).readValue(json);
            log.debug("Объект обновлен из JSON");
        } catch (IOException e) {
            log.error("Ошибка обновления объекта из JSON: {}", e.getMessage(), e);
            throw new ChatException("JSON_009", "Ошибка обновления объекта из JSON", e);
        }
    }
}