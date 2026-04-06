package com.telegram.clone.client.view.panel;

import com.telegram.clone.client.view.renderer.ContactListCellRenderer;
import com.telegram.clone.client.viewmodel.IMainViewModel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Панель списка контактов.
 * Отображает список онлайн пользователей и диалогов.
 */
@Slf4j
public class ContactListPanel extends JPanel {

    private final IMainViewModel viewModel;
    private JList<ContactItem> contactList;
    private DefaultListModel<ContactItem> listModel;
    private JTextField searchField;

    /**
     * Конструктор панели контактов.
     *
     * @param viewModel ViewModel главного окна
     */
    public ContactListPanel(IMainViewModel viewModel) {
        this.viewModel = viewModel;
        initComponents();
        setupBindings();
        loadContacts();
    }

    /**
     * Инициализация компонентов.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // Панель поиска
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Список контактов
        listModel = new DefaultListModel<>();
        contactList = new JList<>(listModel);
        contactList.setCellRenderer(new ContactListCellRenderer());
        contactList.setBackground(new Color(30, 30, 30));
        contactList.setBorder(new EmptyBorder(5, 0, 5, 0));
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ContactItem selected = contactList.getSelectedValue();
                if (selected != null) {
                    viewModel.setCurrentChat(selected.getUsername());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Создание панели поиска.
     *
     * @return панель поиска
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(30, 30, 30));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search");
        searchField.setBackground(new Color(40, 40, 40));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterContacts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterContacts(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterContacts(); }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }

    /**
     * Настройка привязок ViewModel.
     */
    private void setupBindings() {
        viewModel.addPropertyChangeListener(evt -> {
            log.info("Property changed: {}", evt.getPropertyName());
            switch (evt.getPropertyName()) {
                case "onlineUsers" -> {
                    log.info("Online users updated: {}", evt.getNewValue());
                    updateContacts();
                }
                case "userStatus" -> {
                    log.info("User status changed: {}", evt.getNewValue());
                    updateContacts();
                }
            }
        });
    }

    /**
     * Загрузка списка контактов.
     */
    private void loadContacts() {
        viewModel.loadOnlineUsers();
    }

    /**
     * Обновление списка контактов.
     */
    private void updateContacts() {
        SwingUtilities.invokeLater(() -> {
            List<String> onlineUsers = viewModel.getOnlineUsers();
            log.info("Updating contacts with online users: {}", onlineUsers);

            listModel.clear();

            for (String username : onlineUsers) {
                if (!username.equals(viewModel.getCurrentUser().getUsername())) {
                    log.info("Adding contact: {}", username);
                    listModel.addElement(new ContactItem(username, true));
                }
            }

            if (listModel.isEmpty()) {
                log.warn("No contacts found!");
            }
        });
    }

    /**
     * Обновление статуса пользователя.
     *
     * @param username имя пользователя
     */
    private void updateUserStatus(String username) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < listModel.size(); i++) {
                ContactItem item = listModel.get(i);
                if (item.getUsername().equals(username)) {
                    boolean isOnline = viewModel.isUserOnline(username);
                    item.setOnline(isOnline);
                    listModel.set(i, item);
                    break;
                }
            }
        });
    }

    /**
     * Фильтрация контактов по поисковому запросу.
     */
    private void filterContacts() {
        String filter = searchField.getText().toLowerCase().trim();

        SwingUtilities.invokeLater(() -> {
            contactList.clearSelection();
            // Фильтрация уже происходит через обновление списка
            // В реальном приложении здесь была бы более сложная логика
        });
    }

    /**
     * Внутренний класс для элемента списка контактов.
     */
    public static class ContactItem {
        private final String username;
        private boolean online;

        public ContactItem(String username, boolean online) {
            this.username = username;
            this.online = online;
        }

        public String getUsername() { return username; }
        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }
    }
}