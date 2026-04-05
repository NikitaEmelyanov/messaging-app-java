package com.telegram.clone.client.viewmodel;

import com.telegram.clone.client.model.ChatMessage;
import com.telegram.clone.client.model.ClientUser;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * ViewModel для главного окна.
 */
public interface IMainViewModel {

    /**
     * Инициализация подключения.
     */
    void initializeConnection();

    /**
     * Загрузка онлайн пользователей.
     */
    void loadOnlineUsers();

    /**
     * Получение списка онлайн пользователей.
     *
     * @return список имен онлайн пользователей
     */
    List<String> getOnlineUsers();

    /**
     * Проверка, онлайн ли пользователь.
     *
     * @param username имя пользователя
     * @return true если онлайн
     */
    boolean isUserOnline(String username);

    /**
     * Установка текущего чата.
     *
     * @param username имя собеседника
     */
    void setCurrentChat(String username);

    /**
     * Получение текущего чата.
     *
     * @return имя собеседника
     */
    String getCurrentChat();

    /**
     * Получение сообщений для чата.
     *
     * @param username имя собеседника
     * @return список сообщений
     */
    List<ChatMessage> getMessagesForChat(String username);

    /**
     * Отправка сообщения.
     *
     * @param text текст сообщения
     */
    void sendMessage(String text);

    /**
     * Выход из системы.
     */
    void logout();

    /**
     * Получение текущего пользователя.
     *
     * @return текущий пользователь
     */
    ClientUser getCurrentUser();

    /**
     * Добавление слушателя изменений свойств.
     *
     * @param listener слушатель
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Удаление слушателя изменений свойств.
     *
     * @param listener слушатель
     */
    void removePropertyChangeListener(PropertyChangeListener listener);
}