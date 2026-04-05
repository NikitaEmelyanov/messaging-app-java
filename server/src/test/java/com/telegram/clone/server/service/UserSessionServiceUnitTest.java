package com.telegram.clone.server.service;

import com.telegram.clone.server.service.impl.UserSessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserSessionServiceUnitTest {

    private UserSessionServiceImpl sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new UserSessionServiceImpl();
    }

    @Test
    @DisplayName("Должен добавить сессию пользователя")
    void testAddSession() {
        sessionService.addSession("alice", "session123");

        assertThat(sessionService.isUserOnline("alice")).isTrue();
        assertThat(sessionService.getActiveUsersCount()).isEqualTo(1);
        assertThat(sessionService.getSessionId("alice")).isEqualTo("session123");
    }

    @Test
    @DisplayName("Должен удалить сессию пользователя")
    void testRemoveSession() {
        sessionService.addSession("alice", "session123");
        sessionService.removeSession("alice");

        assertThat(sessionService.isUserOnline("alice")).isFalse();
        assertThat(sessionService.getActiveUsersCount()).isEqualTo(0);
        assertThat(sessionService.getSessionId("alice")).isNull();
    }

    @Test
    @DisplayName("Должен получить всех онлайн пользователей")
    void testGetOnlineUsers() {
        sessionService.addSession("alice", "session1");
        sessionService.addSession("bob", "session2");
        sessionService.addSession("charlie", "session3");

        var onlineUsers = sessionService.getOnlineUsers();

        assertThat(onlineUsers).hasSize(3);
        assertThat(onlineUsers).containsExactlyInAnyOrder("alice", "bob", "charlie");
    }

    @Test
    @DisplayName("Должен вернуть false для несуществующего пользователя")
    void testIsUserOnlineForNonExistent() {
        assertThat(sessionService.isUserOnline("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("Должен корректно обновить сессию при повторном входе")
    void testReconnectSameUser() {
        sessionService.addSession("alice", "session1");
        assertThat(sessionService.getSessionId("alice")).isEqualTo("session1");

        sessionService.addSession("alice", "session2");
        assertThat(sessionService.getSessionId("alice")).isEqualTo("session2");
        assertThat(sessionService.getActiveUsersCount()).isEqualTo(1);
    }
}