package com.telegram.clone.server.exception;

import com.telegram.clone.common.exception.ChatException;
import com.telegram.clone.server.broker.MockWebSocketSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockWebSocketSession session;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        session = new MockWebSocketSession("test-session");
    }

    @Test
    @DisplayName("Должен обработать AuthenticationException")
    void testHandleAuthenticationException() {
        ChatException.AuthenticationException exception =
            new ChatException.AuthenticationException("Auth failed");

        handler.handleWebSocketError(session, exception);

        assertThat(session.getSentMessages()).isNotEmpty();
        String message = session.getSentMessages().get(0);
        assertThat(message).contains("Authentication failed");
    }

    @Test
    @DisplayName("Должен обработать ValidationException")
    void testHandleValidationException() {
        ChatException.ValidationException exception =
            new ChatException.ValidationException("Invalid message");

        handler.handleWebSocketError(session, exception);

        assertThat(session.getSentMessages()).isNotEmpty();
        String message = session.getSentMessages().get(0);
        assertThat(message).contains("Invalid message format");
    }

    @Test
    @DisplayName("Должен обработать generic exception")
    void testHandleGenericException() {
        Exception exception = new RuntimeException("Unexpected error");

        handler.handleWebSocketError(session, exception);

        assertThat(session.getSentMessages()).isNotEmpty();
        String message = session.getSentMessages().get(0);
        assertThat(message).contains("Internal server error");
    }
}