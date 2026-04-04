package com.telegram.clone.server.controller;

import com.telegram.clone.common.dto.UserStatusDto;
import com.telegram.clone.server.service.impl.UserSessionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * REST контроллер для управления пользователями.
 * Предоставляет API для получения информации о пользователях и их статусах.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserWebSocketController {

    private final UserSessionServiceImpl sessionService;

    /**
     * Получить список всех онлайн пользователей.
     *
     * @return множество имен онлайн пользователей
     */
    @GetMapping("/online")
    @Operation(summary = "Получить онлайн пользователей",
        description = "Возвращает список всех пользователей, находящихся в сети")
    public Set<String> getOnlineUsers() {
        log.debug("Getting online users list");
        return sessionService.getOnlineUsers();
    }

    /**
     * Получить статус конкретного пользователя.
     *
     * @param username имя пользователя
     * @return DTO со статусом пользователя
     */
    @GetMapping("/{username}/status")
    @Operation(summary = "Получить статус пользователя",
        description = "Возвращает текущий статус указанного пользователя")
    public UserStatusDto getUserStatus(@PathVariable String username) {
        log.debug("Getting status for user: {}", username);
        boolean isOnline = sessionService.isUserOnline(username);
        return isOnline ? UserStatusDto.online(username) : UserStatusDto.offline(username);
    }

    /**
     * Проверить, находится ли пользователь в сети.
     *
     * @param username имя пользователя
     * @return true если пользователь онлайн
     */
    @GetMapping("/{username}/is-online")
    @Operation(summary = "Проверить онлайн статус",
        description = "Проверяет, находится ли пользователь в сети")
    public boolean isUserOnline(@PathVariable String username) {
        return sessionService.isUserOnline(username);
    }
}