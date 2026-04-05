package com.telegram.clone.client.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.telegram.clone.client.model.ClientUser;
import com.telegram.clone.client.service.IChatService;
import com.telegram.clone.client.service.impl.ChatService;
import com.telegram.clone.client.view.panel.ChatPanel;
import com.telegram.clone.client.view.panel.ContactListPanel;
import com.telegram.clone.client.viewmodel.IMainViewModel;
import com.telegram.clone.client.viewmodel.impl.MainViewModel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * Главное окно приложения.
 * Реализует интерфейс в стиле Telegram.
 */
@Slf4j
public class MainFrame extends JFrame {

    private final IMainViewModel viewModel;
    private ContactListPanel contactListPanel;
    private ChatPanel chatPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;

    /**
     * Конструктор главного окна.
     *
     * @param currentUser текущий пользователь
     */
    public MainFrame(ClientUser currentUser) {
        this.viewModel = new MainViewModel(currentUser, new ChatService());
        initComponents();
        setupBindings();
        initializeNetwork();
    }

    /**
     * Инициализация UI компонентов.
     */
    private void initComponents() {
        setTitle("Telegram Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 500));

        // Создание заголовка
        createHeader();

        // Создание основной панели с разделителем
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(1);
        splitPane.setBackground(new Color(30, 30, 30));

        // Панель контактов (слева)
        contactListPanel = new ContactListPanel(viewModel);
        splitPane.setLeftComponent(contactListPanel);

        // Панель чата (справа)
        chatPanel = new ChatPanel(viewModel);
        splitPane.setRightComponent(chatPanel);

        add(splitPane, BorderLayout.CENTER);

        // Статус бар
        createStatusBar();
    }

    /**
     * Создание заголовка окна.
     */
    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 34, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        titleLabel = new JLabel("Telegram");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Кнопки управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton settingsBtn = createIconButton("⚙️");
        JButton logoutBtn = createIconButton("🚪");
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(settingsBtn);
        buttonPanel.add(logoutBtn);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Создание кнопки с иконкой.
     *
     * @param icon текст иконки
     * @return кнопка
     */
    private JButton createIconButton(String icon) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        return button;
    }

    /**
     * Создание статус бара.
     */
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(34, 34, 34));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setPreferredSize(new Dimension(getWidth(), 30));

        JLabel statusLabel = new JLabel("Connected");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 200, 100));
        statusBar.add(statusLabel, BorderLayout.WEST);

        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Настройка привязок ViewModel.
     */
    private void setupBindings() {
        viewModel.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "currentChat" -> onCurrentChatChanged();
                case "error" -> showError((String) evt.getNewValue());
            }
        });
    }

    /**
     * Инициализация сетевого подключения.
     */
    private void initializeNetwork() {
        viewModel.initializeConnection();
    }

    /**
     * Обработка смены текущего чата.
     */
    private void onCurrentChatChanged() {
        String currentChat = viewModel.getCurrentChat();
        if (currentChat != null) {
            titleLabel.setText("Chat with " + currentChat);
            chatPanel.loadMessages(currentChat);
        } else {
            titleLabel.setText("Telegram");
        }
    }

    /**
     * Показать ошибку.
     *
     * @param message сообщение об ошибке
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Выход из системы.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            viewModel.logout();
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.setVisible(true);
            });
        }
    }
}