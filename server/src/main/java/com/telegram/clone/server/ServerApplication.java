package com.telegram.clone.server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Главный класс серверного приложения чата Telegram Clone.
 * Запускает Spring Boot приложение с WebSocket поддержкой.
 */
@SpringBootApplication(scanBasePackages = "com.telegram.clone.server")
@EnableAsync
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "Telegram Clone Chat API",
        version = "1.0.0",
        description = "API для серверной части мессенджера Telegram Clone",
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Local server",
            url = "http://localhost:8080"
        )
    }
)
public class ServerApplication {

    /**
     * Точка входа в приложение сервера.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}