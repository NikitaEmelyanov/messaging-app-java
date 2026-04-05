package com.telegram.clone.client.view.panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Панель ввода сообщения с дополнительными функциями.
 */
public class MessageInputPanel extends JPanel {

    private JTextArea textArea;
    private JButton sendButton;
    private JButton emojiButton;
    private JButton attachButton;

    /**
     * Конструктор панели ввода.
     */
    public MessageInputPanel() {
        initComponents();
    }

    /**
     * Инициализация компонентов.
     */
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Кнопки слева
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftButtons.setOpaque(false);

        emojiButton = createIconButton("😊");
        attachButton = createIconButton("📎");

        leftButtons.add(emojiButton);
        leftButtons.add(attachButton);

        // Текстовая область
        textArea = new JTextArea(3, 30);
        textArea.setBackground(new Color(40, 40, 40));
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(0, 60));

        // Кнопка отправки
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(64, 128, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(80, 40));

        // Компоновка
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setOpaque(false);
        centerPanel.add(leftButtons, BorderLayout.WEST);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(sendButton, BorderLayout.EAST);
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
        return button;
    }

    /**
     * Получить текст сообщения.
     *
     * @return текст сообщения
     */
    public String getMessageText() {
        return textArea.getText().trim();
    }

    /**
     * Очистить поле ввода.
     */
    public void clear() {
        textArea.setText("");
    }

    /**
     * Установить слушатель отправки сообщения.
     *
     * @param listener слушатель
     */
    public void setOnSendListener(Runnable listener) {
        sendButton.addActionListener(e -> listener.run());
        textArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && e.isControlDown()) {
                    listener.run();
                }
            }
        });
    }
}