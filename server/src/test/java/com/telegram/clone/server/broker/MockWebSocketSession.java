package com.telegram.clone.server.broker;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockWebSocketSession implements WebSocketSession {

    private final String id;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private final List<String> sentMessages = new ArrayList<>();
    private boolean open = true;

    public MockWebSocketSession(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public URI getUri() {
        return URI.create("ws://localhost:8080/ws/chat");
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Principal getPrincipal() {
        return () -> (String) attributes.get("username");
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress("localhost", 8080);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress("localhost", 12345);
    }

    @Override
    public String getAcceptedProtocol() {
        return null;
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {
    }

    @Override
    public int getTextMessageSizeLimit() {
        return 1024 * 1024;
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {
    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return 1024 * 1024;
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return List.of();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void sendMessage(WebSocketMessage<?> message) throws IOException {
        if (!open) {
            throw new IOException("Session is closed");
        }
        sentMessages.add(message.getPayload().toString());
    }

    @Override
    public void close() throws IOException {
        close(CloseStatus.NORMAL);
    }

    @Override
    public void close(CloseStatus status) throws IOException {
        this.open = false;
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clearSentMessages() {
        sentMessages.clear();
    }
}