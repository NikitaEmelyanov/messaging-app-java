package com.telegram.clone.client.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.clone.client.model.ClientUser;
import com.telegram.clone.client.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import java.util.Map;

/**
 * Реализация сервиса аутентификации.
 * Отвечает за вход в систему, выход и валидацию токенов.
 */
@Slf4j
public class AuthService implements IAuthService {

    private static final String SERVER_URL = "http://localhost:8080";
    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;

    /** Конструктор сервиса аутентификации. */
    public AuthService() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientUser login(String username, String password) {
        try {
            HttpPost request = new HttpPost(SERVER_URL + "/api/auth/login");
            request.setHeader("Content-Type", "application/json");

            Map<String, String> body = Map.of("username", username, "password", password);
            String jsonBody = objectMapper.writeValueAsString(body);
            request.setEntity(new StringEntity(jsonBody));

            try (var response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);

                boolean success = (Boolean) result.getOrDefault("success", false);
                if (success) {
                    String token = (String) result.get("token");
                    String displayName = (String) result.get("displayName");

                    log.info("User logged in successfully: {}", username);
                    return ClientUser.fromAuthResponse(username, displayName, token);
                } else {
                    String message = (String) result.getOrDefault("message", "Unknown error");
                    log.warn("Login failed for {}: {}", username, message);
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("Login error for user: {}", username, e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout(String token) {
        try {
            HttpPost request = new HttpPost(SERVER_URL + "/api/auth/logout?token=" + token);
            httpClient.execute(request);
            log.info("User logged out with token: {}", token);
        } catch (Exception e) {
            log.error("Logout error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateToken(String token) {
        try {
            HttpPost request = new HttpPost(SERVER_URL + "/api/auth/validate?token=" + token);
            try (var response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return Boolean.parseBoolean(responseBody);
            }
        } catch (Exception e) {
            log.error("Token validation error", e);
            return false;
        }
    }
}