package com.telegram.clone.client.viewmodel;

import com.telegram.clone.client.model.ClientUser;

import java.beans.PropertyChangeListener;

/**
 * ViewModel для окна входа.
 */
public interface ILoginViewModel {

    /**
     * Вход в систему.
     *
     * @param username имя пользователя
     * @param password пароль
     */
    void login(String username, String password);

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