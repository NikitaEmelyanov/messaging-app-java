package com.telegram.clone.client.view.panel;

import com.telegram.clone.client.model.ChatMessage;
import com.telegram.clone.client.view.renderer.MessageListCellRenderer;
import com.telegram.clone.client.viewmodel.IMainViewModel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Панель чата.
 * Отображает историю сообщений и поле ввода.
 */
@Slf4j
public class ChatPanel extends JPanel {

    private final IMainViewModel viewModel;
    private JList<ChatMessage> messageList;
    private DefaultListModel<ChatMessage> listModel;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel typingLabel;

    /**
     * Конструктор панели чата.
     *
     * @param viewModel ViewModel главного окна
     */
    public ChatPanel(IMainViewModel viewModel) {
        this.viewModel = viewModel;
        initComponents();
        setupBindings();
    }

    /**
     * Инициализация компонентов.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(22, 22, 22));

        // Область сообщений
        listModel = new DefaultListModel<>();
        messageList = new JList<>(listModel);
        messageList.setCellRenderer(new MessageListCellRenderer());
        messageList.setBackground(new Color(22, 22, 22));
        messageList.setBorder(new EmptyBorder(10, 10, 10, 10));
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(22, 22, 22));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Панель ввода сообщения
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.SOUTH);

        // Индикатор печатания
        typingLabel = new JLabel(" ");
        typingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        typingLabel.setForeground(new Color(150, 150, 150));
        typingLabel.setBorder(new EmptyBorder(5, 10, 0, 10));
        add(typingLabel, BorderLayout.NORTH);
    }

    /**
     * Создание панели ввода сообщения.
     *
     * @return панель ввода
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(30, 30, 30));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputField = new JTextField();
        inputField.setBackground(new Color(40, 40, 40));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.putClientProperty("JTextField.placeholderText", "Type a message...");
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        inputField.addActionListener(e -> sendMessage());

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(64, 128, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        return inputPanel;
    }

    /**
     * Настройка привязок ViewModel.
     */
    private void setupBindings() {
        viewModel.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "messages" -> updateMessages();
                case "newMessage" -> addNewMessage((ChatMessage) evt.getNewValue());
                case "typing" -> updateTypingIndicator((Boolean) evt.getNewValue());
            }
        });
    }

    /**
     * Загрузка сообщений для чата.
     *
     * @param username имя собеседника
     */
    public void loadMessages(String username) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            List<ChatMessage> messages = viewModel.getMessagesForChat(username);
            for (ChatMessage message : messages) {
                listModel.addElement(message);
            }
            scrollToBottom();
        });
    }

    /**
     * Обновление списка сообщений.
     */
    private void updateMessages() {
        SwingUtilities.invokeLater(() -> {
            String currentChat = viewModel.getCurrentChat();
            if (currentChat != null) {
                loadMessages(currentChat);
            }
        });
    }

    /**
     * Добавление нового сообщения.
     *
     * @param message новое сообщение
     */
    private void addNewMessage(ChatMessage message) {
        SwingUtilities.invokeLater(() -> {
            listModel.addElement(message);
            scrollToBottom();
        });
    }

    /**
     * Обновление индикатора печатания.
     *
     * @param isTyping флаг печатания
     */
    private void updateTypingIndicator(Boolean isTyping) {
        SwingUtilities.invokeLater(() -> {
            if (isTyping) {
                typingLabel.setText(viewModel.getCurrentChat() + " is typing...");
            } else {
                typingLabel.setText(" ");
            }
        });
    }

    /**
     * Отправка сообщения.
     */
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        viewModel.sendMessage(text);
        inputField.setText("");
        inputField.requestFocus();
    }

    /**
     * Прокрутка к последнему сообщению.
     */
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            int lastIndex = listModel.getSize() - 1;
            if (lastIndex >= 0) {
                messageList.ensureIndexIsVisible(lastIndex);
            }
        });
    }
}