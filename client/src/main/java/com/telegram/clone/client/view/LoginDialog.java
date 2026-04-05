package com.telegram.clone.client.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.telegram.clone.client.service.IAuthService;
import com.telegram.clone.client.service.impl.AuthService;
import com.telegram.clone.client.viewmodel.ILoginViewModel;
import com.telegram.clone.client.viewmodel.impl.LoginViewModel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * Диалог входа в систему.
 * Стилизован под Telegram темную тему.
 */
@Slf4j
public class LoginDialog extends JDialog {

    private final ILoginViewModel viewModel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    /**
     * Конструктор диалога входа.
     */
    public LoginDialog() {
        this.viewModel = new LoginViewModel(new AuthService());
        initComponents();
        setupBindings();
    }

    /**
     * Инициализация UI компонентов.
     */
    private void initComponents() {
        setTitle("Telegram Login");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Главная панель
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 40, 10, 40);

        // Логотип/аватар
        JLabel logoLabel = new JLabel("📱", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        logoLabel.setForeground(new Color(64, 128, 255));
        mainPanel.add(logoLabel, gbc);

        // Заголовок
        JLabel titleLabel = new JLabel("Telegram Clone", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, gbc);

        // Пустое пространство
        gbc.insets = new Insets(20, 40, 10, 40);
        mainPanel.add(Box.createVerticalStrut(20), gbc);

        // Поле имени пользователя
        usernameField = new JTextField();
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleTextField(usernameField);
        mainPanel.add(usernameField, gbc);

        // Поле пароля
        passwordField = new JPasswordField();
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleTextField(passwordField);
        mainPanel.add(passwordField, gbc);

        // Статус
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(255, 100, 100));
        mainPanel.add(statusLabel, gbc);

        // Кнопка входа
        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(64, 128, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(loginButton, gbc);

        // Информация о тестовых пользователях
        gbc.insets = new Insets(30, 40, 10, 40);
        JLabel infoLabel = new JLabel(
            "<html><center>Test users:<br>alice/pass123 | bob/qwerty | charlie/123456</center></html>",
            SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(150, 150, 150));
        mainPanel.add(infoLabel, gbc);

        add(mainPanel);

        // Обработчики событий
        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
    }

    /**
     * Стилизация текстового поля.
     *
     * @param field текстовое поле
     */
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(40, 40, 40));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    /**
     * Настройка привязок ViewModel.
     */
    private void setupBindings() {
        viewModel.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "loading" -> updateLoadingState((Boolean) evt.getNewValue());
                case "error" -> showError((String) evt.getNewValue());
                case "success" -> onLoginSuccess();
            }
        });
    }

    /**
     * Выполнить вход.
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        viewModel.login(username, password);
    }

    /**
     * Обновить состояние загрузки.
     *
     * @param loading флаг загрузки
     */
    private void updateLoadingState(boolean loading) {
        loginButton.setEnabled(!loading);
        loginButton.setText(loading ? "Connecting..." : "Sign In");
        usernameField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
    }

    /**
     * Показать ошибку.
     *
     * @param message сообщение об ошибке
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);

        Timer timer = new Timer(3000, e -> statusLabel.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Обработка успешного входа.
     */
    private void onLoginSuccess() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(viewModel.getCurrentUser());
            mainFrame.setVisible(true);
        });
    }
}