package com.telegram.clone.server.repository.impl;

import com.telegram.clone.common.enums.UserStatus;
import com.telegram.clone.common.model.User;
import com.telegram.clone.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory реализация репозитория пользователей.
 * Хранит пользователей в ConcurrentHashMap с предопределенными тестовыми данными.
 */
@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final Map<String, User> usersById = new ConcurrentHashMap<>();

    /**
     * Конструктор с инициализацией тестовых пользователей.
     */
    public InMemoryUserRepository() {
        initializeTestUsers();
    }

    /**
     * Инициализация предопределенных тестовых пользователей.
     */
    private void initializeTestUsers() {
        log.info("Initializing test users...");

        List<User> testUsers = List.of(
            createTestUser("alice", "pass123", "Alice Wonderland"),
            createTestUser("bob", "qwerty", "Bob Builder"),
            createTestUser("charlie", "123456", "Charlie Brown"),
            createTestUser("dmitry", "letmein", "Dmitry Ivanov"),
            createTestUser("elena", "password", "Elena Petrova"),
            createTestUser("michael", "mike123", "Michael Smith"),
            createTestUser("anna", "anna2024", "Anna Johnson")
        );

        testUsers.forEach(this::save);
        log.info("Initialized {} test users", testUsers.size());
    }

    /**
     * Создание тестового пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @param displayName отображаемое имя
     * @return созданный пользователь
     */
    private User createTestUser(String username, String password, String displayName) {
        return User.builder()
            .id(UUID.randomUUID().toString())
            .username(username)
            .passwordHash(hashPassword(password))
            .displayName(displayName)
            .status(UserStatus.OFFLINE)
            .lastActiveTime(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * Хэширование пароля.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Password hashing failed", e);
            return password;
        }
    }

    /**
     * Найти пользователя по имени.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username.toLowerCase()));
    }

    /**
     * Найти пользователя по идентификатору.
     */
    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    /**
     * Сохранить или обновить пользователя.
     */
    @Override
    public User save(User user) {
        usersByUsername.put(user.getUsername().toLowerCase(), user);
        usersById.put(user.getId(), user);
        log.debug("User saved: {}", user.getUsername());
        return user;
    }

    /**
     * Удалить пользователя по имени.
     */
    @Override
    public boolean deleteByUsername(String username) {
        User removed = usersByUsername.remove(username.toLowerCase());
        if (removed != null) {
            usersById.remove(removed.getId());
            log.info("User deleted: {}", username);
            return true;
        }
        return false;
    }

    /**
     * Получить всех пользователей.
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersByUsername.values());
    }

    /**
     * Получить всех онлайн пользователей.
     */
    @Override
    public List<User> findAllOnline() {
        return usersByUsername.values().stream()
            .filter(User::isOnline)
            .collect(Collectors.toList());
    }

    /**
     * Проверить существование пользователя.
     */
    @Override
    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username.toLowerCase());
    }

    /**
     * Получить количество пользователей.
     */
    @Override
    public long count() {
        return usersByUsername.size();
    }

    /**
     * Обновить статус пользователя.
     */
    @Override
    public User updateStatus(String username, boolean online) {
        User user = usersByUsername.get(username.toLowerCase());
        if (user != null) {
            user.setStatus(online ? UserStatus.ONLINE : UserStatus.OFFLINE);
            user.updateLastActive();
            log.debug("User status updated: {} -> {}", username, online ? "ONLINE" : "OFFLINE");
        }
        return user;
    }

    /**
     * Получить всех пользователей, чьи имена начинаются с префикса.
     */
    @Override
    public List<User> findByUsernameStartingWith(String prefix) {
        return usersByUsername.values().stream()
            .filter(user -> user.getUsername().toLowerCase().startsWith(prefix.toLowerCase()))
            .collect(Collectors.toList());
    }

    /**
     * Получить список пользователей по списку имен.
     */
    @Override
    public List<User> findByUsernames(Set<String> usernames) {
        return usernames.stream()
            .map(this::findByUsername)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
}