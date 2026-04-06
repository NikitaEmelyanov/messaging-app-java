package com.telegram.clone.client.viewmodel.impl;

import com.telegram.clone.client.model.ChatMessage;
import com.telegram.clone.client.model.ClientUser;
import com.telegram.clone.client.network.listener.NetworkListener;
import com.telegram.clone.client.service.IChatService;
import com.telegram.clone.client.service.IMessageService;
import com.telegram.clone.client.service.impl.MessageService;
import com.telegram.clone.client.viewmodel.IMainViewModel;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация ViewModel для главного окна.
 * Управляет состоянием UI, сетевыми подключениями и бизнес-логикой.
 */
@Slf4j
public class MainViewModel implements IMainViewModel, NetworkListener, IMessageService.MessageListener {

    private final ClientUser currentUser;
    private final IChatService chatService;
    private final IMessageService messageService;
    private final PropertyChangeSupport propertyChangeSupport;
    private final Set<String> onlineUsers;
    private String currentChat;
    private boolean connected;

    /**
     * Конструктор ViewModel.
     *
     * @param currentUser текущий пользователь
     * @param chatService сервис чата
     */
    public MainViewModel(ClientUser currentUser, IChatService chatService) {
        this.currentUser = currentUser;
        this.chatService = chatService;
        this.messageService = new MessageService();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.onlineUsers = ConcurrentHashMap.newKeySet();
        this.connected = false;

        this.messageService.addMessageListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void initializeConnection() {
        chatService.connect(currentUser, this);
    }

    /** {@inheritDoc} */
    @Override
    public void loadOnlineUsers() {
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getOnlineUsers() {
        return new ArrayList<>(onlineUsers);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUserOnline(String username) {
        return onlineUsers.contains(username);
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrentChat(String username) {
        String oldValue = this.currentChat;
        this.currentChat = username;
        propertyChangeSupport.firePropertyChange("currentChat", oldValue, username);
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrentChat() {
        return currentChat;
    }

    /** {@inheritDoc} */
    @Override
    public List<ChatMessage> getMessagesForChat(String username) {
        return messageService.getMessagesForChat(username);
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(String text) {
        if (currentChat != null && !text.isEmpty()) {
            ChatMessage message = chatService.sendMessage(currentChat, text);
            messageService.onMessageReceived(message);
            propertyChangeSupport.firePropertyChange("newMessage", null, message);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void logout() {
        chatService.disconnect();
    }

    /** {@inheritDoc} */
    @Override
    public ClientUser getCurrentUser() {
        return currentUser;
    }

    /** {@inheritDoc} */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    // NetworkListener реализация
    /** {@inheritDoc} */
    @Override
    public void onConnected() {
        this.connected = true;
        log.info("Connected to server via WebSocket");
        propertyChangeSupport.firePropertyChange("connected", false, true);

        // ЗАГРУЖАЕМ ОНЛАЙН ПОЛЬЗОВАТЕЛЕЙ СРАЗУ ПОСЛЕ ПОДКЛЮЧЕНИЯ
        loadOnlineUsers();

        // Также запрашиваем статусы через REST API
        fetchOnlineUsersViaRest();
    }

    @Override
    public void onDisconnected() {

    }

    private void fetchOnlineUsersViaRest() {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("http://localhost:8080/api/users/online");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    java.io.BufferedReader br = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream())
                    );
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    System.out.println("REST response: " + response.toString());

                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

                    java.util.List<String> users = mapper.readValue(response.toString(),
                            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});

                    System.out.println("Online users from REST: " + users);

                    // Очищаем и обновляем список
                    onlineUsers.clear();
                    for (String username : users) {
                        if (!username.equals(currentUser.getUsername())) {
                            onlineUsers.add(username);
                        }
                    }

                    // Принудительно обновляем UI
                    propertyChangeSupport.firePropertyChange("onlineUsers", null, new java.util.ArrayList<>(onlineUsers));
                }
                conn.disconnect();
            } catch (Exception e) {
                System.err.println("Failed to fetch online users: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(ChatMessage message) {
        messageService.onMessageReceived(message);
        propertyChangeSupport.firePropertyChange("newMessage", null, message);
    }

    /** {@inheritDoc} */
    @Override
    public void onMessageSent(ChatMessage message) {
    }

    /** {@inheritDoc} */
    @Override
    public void onSystemMessage(String message) {
        log.info("System message: {}", message);
        propertyChangeSupport.firePropertyChange("systemMessage", null, message);
    }

    /** {@inheritDoc} */
    @Override
    public void onError(String error) {
        log.error("Network error: {}", error);
        propertyChangeSupport.firePropertyChange("error", null, error);
    }

    /** {@inheritDoc} */
    @Override
    public void onUserStatusChanged(String username, boolean online) {
        log.info("User status changed: {} -> {}", username, online ? "online" : "offline");
        if (online) {
            onlineUsers.add(username);
        } else {
            onlineUsers.remove(username);
        }
        propertyChangeSupport.firePropertyChange("userStatus", null, username);
        propertyChangeSupport.firePropertyChange("onlineUsers", null, onlineUsers);
    }

    /** {@inheritDoc} */
    @Override
    public void onTypingStatusChanged(String username, boolean isTyping) {
        if (currentChat != null && currentChat.equals(username)) {
            propertyChangeSupport.firePropertyChange("typing", null, isTyping);
        }
    }

    // MessageListener реализация
    /** {@inheritDoc} */
    @Override
    public void onNewMessage(ChatMessage message) {
        String chatPartner = message.isFromMe() ? message.getTo() : message.getFrom();
        if (currentChat != null && currentChat.equals(chatPartner)) {
            propertyChangeSupport.firePropertyChange("newMessage", null, message);
        }
    }
}