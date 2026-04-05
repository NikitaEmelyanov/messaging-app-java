package com.telegram.clone.server.repository;

import com.telegram.clone.server.model.UserSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользовательскими сессиями.
 * Управляет жизненным циклом сессий пользователей.
 */
public interface SessionRepository {

    /**
     * Создать новую сессию.
     *
     * @param session сессия для создания
     * @return созданная сессия
     */
    UserSession createSession(UserSession session);

    /**
     * Найти сессию по токену.
     *
     * @param token токен сессии
     * @return Optional с сессией
     */
    Optional<UserSession> findByToken(String token);

    /**
     * Найти сессию по имени пользователя.
     *
     * @param username имя пользователя
     * @return Optional с сессией
     */
    Optional<UserSession> findByUsername(String username);

    /**
     * Удалить сессию по токену.
     *
     * @param token токен сессии
     * @return true если сессия была удалена
     */
    boolean deleteByToken(String token);

    /**
     * Удалить сессию по имени пользователя.
     *
     * @param username имя пользователя
     * @return true если сессия была удалена
     */
    boolean deleteByUsername(String username);

    /**
     * Получить все активные сессии.
     *
     * @return список активных сессий
     */
    List<UserSession> findAllActive();

    /**
     * Получить количество активных сессий.
     *
     * @return количество активных сессий
     */
    long countActive();

    /**
     * Обновить время последней активности сессии.
     *
     * @param token токен сессии
     * @return обновленная сессия
     */
    UserSession updateLastActivity(String token);

    /**
     * Инвалидировать просроченные сессии.
     *
     * @return количество инвалидированных сессий
     */
    int invalidateExpiredSessions();

    /**
     * Проверить, активна ли сессия.
     *
     * @param token токен сессии
     * @return true если сессия активна
     */
    boolean isActive(String token);

    /**
     * Получить все сессии, истекшие до указанного времени.
     *
     * @param time время для сравнения
     * @return список истекших сессий
     */
    List<UserSession> findExpiredSessions(LocalDateTime time);
}