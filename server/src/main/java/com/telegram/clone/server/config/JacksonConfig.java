package com.telegram.clone.server.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Конфигурация Jackson ObjectMapper для сериализации/десериализации JSON.
 * Настраивает форматирование дат, обработку null значений и другие параметры.
 */
@Configuration
public class JacksonConfig {

    /**
     * Создает настроенный ObjectMapper для всего приложения.
     *
     * @return настроенный ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрация модуля для работы с Java 8+ временем
        mapper.registerModule(new JavaTimeModule());

        // Отключение записи дат как timestamp
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Включение форматированного вывода для удобства чтения
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Игнорирование неизвестных свойств при десериализации
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Не включать null значения в JSON
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);

        return mapper;
    }
}