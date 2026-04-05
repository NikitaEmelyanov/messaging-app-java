package com.telegram.clone.client.view.renderer;

import com.telegram.clone.client.view.panel.ContactListPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Рендерер для списка контактов.
 * Отображает аватар, имя и статус пользователя.
 */
public class ContactListCellRenderer extends JPanel implements ListCellRenderer<ContactListPanel.ContactItem> {

    private final JLabel avatarLabel;
    private final JLabel nameLabel;
    private final JLabel statusLabel;
    private final JLabel unreadLabel;

    /**
     * Конструктор рендерера.
     */
    public ContactListCellRenderer() {
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(30, 30, 30));

        // Аватар
        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        // Информационная панель
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(150, 150, 150));

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(statusLabel, BorderLayout.SOUTH);

        // Счетчик непрочитанных
        unreadLabel = new JLabel();
        unreadLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        unreadLabel.setForeground(Color.WHITE);
        unreadLabel.setBackground(new Color(64, 128, 255));
        unreadLabel.setOpaque(true);
        unreadLabel.setHorizontalAlignment(SwingConstants.CENTER);
        unreadLabel.setPreferredSize(new Dimension(20, 20));

        add(avatarLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
        add(unreadLabel, BorderLayout.EAST);
    }

    /**
     * Получение компонента для отображения элемента списка.
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends ContactListPanel.ContactItem> list,
        ContactListPanel.ContactItem value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {

        String username = value.getUsername();
        boolean isOnline = value.isOnline();

        // Аватар (первая буква имени)
        String firstLetter = username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase();
        avatarLabel.setText(firstLetter);

        // Имя
        nameLabel.setText(username);

        // Статус
        statusLabel.setText(isOnline ? "● Online" : "○ Offline");
        statusLabel.setForeground(isOnline ? new Color(100, 200, 100) : new Color(150, 150, 150));

        // Выделение выбранного элемента
        if (isSelected) {
            setBackground(new Color(50, 50, 50));
        } else {
            setBackground(new Color(30, 30, 30));
        }

        // Счетчик непрочитанных (пока 0)
        unreadLabel.setVisible(false);

        return this;
    }
}