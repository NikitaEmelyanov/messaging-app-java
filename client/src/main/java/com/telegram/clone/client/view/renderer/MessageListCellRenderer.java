package com.telegram.clone.client.view.renderer;

import com.telegram.clone.client.model.ChatMessage;

import javax.swing.*;
import java.awt.*;

/**
 * Рендерер для списка сообщений.
 * Отображает сообщения в стиле Telegram (пузырьки).
 */
public class MessageListCellRenderer extends JPanel implements ListCellRenderer<ChatMessage> {

    private final JLabel messageLabel;
    private final JLabel timeLabel;
    private final JLabel statusLabel;

    /**
     * Конструктор рендерера.
     */
    public MessageListCellRenderer() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Панель для сообщения
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setOpaque(false);

        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(150, 150, 150));

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        footerPanel.setOpaque(false);
        footerPanel.add(timeLabel);
        footerPanel.add(statusLabel);

        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(footerPanel, BorderLayout.SOUTH);

        add(messagePanel, BorderLayout.CENTER);
    }

    /**
     * Получение компонента для отображения элемента списка.
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends ChatMessage> list,
        ChatMessage value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {

        boolean isFromMe = value.isFromMe();

        // Текст сообщения
        String text = value.getContent();
        if (text.length() > 200) {
            text = text.substring(0, 197) + "...";
        }
        messageLabel.setText(text);

        // Время
        timeLabel.setText(value.getFormattedTime());

        // Статус
        String statusText = switch (value.getStatus()) {
            case SENDING -> "⏳";
            case SENT -> "✓";
            case DELIVERED -> "✓✓";
            case FAILED -> "⚠️";
        };
        statusLabel.setText(statusText);

        // Стиль в зависимости от отправителя
        if (isFromMe) {
            setBackground(new Color(64, 128, 255));
            messageLabel.setForeground(Color.WHITE);
            messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setBackground(new Color(40, 40, 40));
            messageLabel.setForeground(Color.WHITE);
            messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }

        // Скругленные углы
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isFromMe ? new Color(64, 128, 255) : new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        return this;
    }
}