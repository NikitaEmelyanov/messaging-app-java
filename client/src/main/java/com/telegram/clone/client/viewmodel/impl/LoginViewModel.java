package com.telegram.clone.client.viewmodel.impl;

import com.telegram.clone.client.model.ClientUser;
import com.telegram.clone.client.service.IAuthService;
import com.telegram.clone.client.viewmodel.ILoginViewModel;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Реализация ViewModel для окна входа.
 */
@Slf4j
public class LoginViewModel implements ILoginViewModel {

    private final IAuthService authService;
    private final PropertyChangeSupport propertyChangeSupport;
    private ClientUser currentUser;
    private boolean loading;
    private String error;

    /**
     * Конструктор ViewModel.
     *
     * @param authService сервис аутентификации
     */
    public LoginViewModel(IAuthService authService) {
        this.authService = authService;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.loading = false;
    }

    /**
     * Вход в систему.
     *
     * @param username имя пользователя
     * @param password пароль
     */
    @Override
    public void login(String username, String password) {
        setLoading(true);
        setError(null);

        // Запуск в отдельном потоке
        new Thread(() -> {
            try {
                ClientUser user = authService.login(username, password);

                if (user != null) {
                    this.currentUser = user;
                    propertyChangeSupport.firePropertyChange("success", null, true);
                } else {
                    setError("Invalid username or password");
                }
            } catch (Exception e) {
                log.error("Login error", e);
                setError("Connection error: " + e.getMessage());
            } finally {
                setLoading(false);
            }
        }).start();
    }

    /**
     * Получение текущего пользователя.
     *
     * @return текущий пользователь
     */
    @Override
    public ClientUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Установка состояния загрузки.
     *
     * @param loading флаг загрузки
     */
    private void setLoading(boolean loading) {
        boolean oldValue = this.loading;
        this.loading = loading;
        propertyChangeSupport.firePropertyChange("loading", oldValue, loading);
    }

    /**
     * Установка ошибки.
     *
     * @param error сообщение об ошибке
     */
    private void setError(String error) {
        String oldValue = this.error;
        this.error = error;
        propertyChangeSupport.firePropertyChange("error", oldValue, error);
    }

    /**
     * Добавление слушателя изменений свойств.
     *
     * @param listener слушатель
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Удаление слушателя изменений свойств.
     *
     * @param listener слушатель
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}