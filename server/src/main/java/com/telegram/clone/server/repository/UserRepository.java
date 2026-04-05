package com.telegram.clone.server.repository;

import com.telegram.clone.common.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Репозиторий для работы с пользователями.
 * Предоставляет методы для поиска, сохранения и управления пользователями.
 */
public interface UserRepository {

    /**
     * Найти пользователя по имени.
     *
     * @param username имя пользователя
     * @return Optional с пользователем или пустой Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * Найти пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return Optional с пользователем или пустой Optional
     */
    Optional<User> findById(String id);

    /**
     * Сохранить или обновить пользователя.
     *
     * @param user пользователь для сохранения
     * @return сохраненный пользователь
     */
    User save(User user);

    /**
     * Удалить пользователя по имени.
     *
     * @param username имя пользователя
     * @return true если пользователь был удален
     */
    boolean deleteByUsername(String username);

    /**
     * Получить всех пользователей.
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Получить всех онлайн пользователей.
     *
     * @return список онлайн пользователей
     */
    List<User> findAllOnline();

    /**
     * Проверить существование пользователя.
     *
     * @param username имя пользователя
     * @return true если пользователь существует
     */
    boolean existsByUsername(String username);

    /**
     * Получить количество пользователей.
     *
     * @return количество пользователей
     */
    long count();

    /**
     * Обновить статус пользователя.
     *
     * @param username имя пользователя
     * @param online новый статус
     * @return обновленный пользователь
     */
    User updateStatus(String username, boolean online);

    /**
     * Получить всех пользователей, чьи имена начинаются с префикса.
     *
     * @param prefix префикс для поиска
     * @return список пользователей
     */
    List<User> findByUsernameStartingWith(String prefix);

    /**
     * Получить список пользователей по списку имен.
     *
     * @param usernames список имен пользователей
     * @return список пользователей
     */
    List<User> findByUsernames(Set<String> usernames);
}