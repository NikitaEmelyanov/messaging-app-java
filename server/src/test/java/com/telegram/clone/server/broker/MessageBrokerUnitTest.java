package com.telegram.clone.server.broker;

import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.common.enums.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class MessageBrokerUnitTest {

    private MessageBroker messageBroker;
    private MockWebSocketSession session1;
    private MockWebSocketSession session2;

    @BeforeEach
    void setUp() {
        messageBroker = new MessageBroker();
        session1 = new MockWebSocketSession("session1");
        session2 = new MockWebSocketSession("session2");
    }

    @Test
    @DisplayName("Должен зарегистрировать сессию пользователя")
    void testRegisterSession() {
        messageBroker.registerSession("alice", session1);

        assertThat(messageBroker.getActiveConnectionsCount()).isEqualTo(1);
        assertThat(messageBroker.isUserOnline("alice")).isTrue();
    }

    @Test
    @DisplayName("Должен удалить сессию пользователя")
    void testUnregisterSession() {
        messageBroker.registerSession("alice", session1);
        messageBroker.unregisterSession("alice");

        assertThat(messageBroker.getActiveConnectionsCount()).isEqualTo(0);
        assertThat(messageBroker.isUserOnline("alice")).isFalse();
    }

    @Test
    @DisplayName("Должен отправить сообщение конкретному пользователю")
    void testSendToUser() {
        messageBroker.registerSession("alice", session1);

        MessageDto message = new MessageDto("1", "bob", "alice", "Hello!", MessageType.TEXT, null, true);
        boolean sent = messageBroker.sendToUser("alice", message);

        assertThat(sent).isTrue();
        assertThat(session1.getSentMessages()).hasSize(1);
        assertThat(session1.getSentMessages().get(0)).contains("Hello!");
    }

    @Test
    @DisplayName("Не должен отправлять сообщение оффлайн пользователю")
    void testSendToOfflineUser() {
        MessageDto message = new MessageDto("1", "bob", "alice", "Hello!", MessageType.TEXT, null, true);
        boolean sent = messageBroker.sendToUser("alice", message);

        assertThat(sent).isFalse();
    }

    @Test
    @DisplayName("Должен разослать сообщение всем пользователям")
    void testBroadcastToAll() {
        messageBroker.registerSession("alice", session1);
        messageBroker.registerSession("bob", session2);

        MessageDto message = new MessageDto("1", "system", null, "Hello all!", MessageType.SYSTEM, null, false);
        int sentCount = messageBroker.broadcastToAll(message, null);

        assertThat(sentCount).isEqualTo(2);
        assertThat(session1.getSentMessages()).hasSize(1);
        assertThat(session2.getSentMessages()).hasSize(1);
        assertThat(session1.getSentMessages().get(0)).contains("Hello all!");
    }

    @Test
    @DisplayName("Должен исключить пользователя из рассылки")
    void testBroadcastWithExclusion() {
        messageBroker.registerSession("alice", session1);
        messageBroker.registerSession("bob", session2);

        MessageDto message = new MessageDto("1", "system", null, "Hello all!", MessageType.SYSTEM, null, false);
        int sentCount = messageBroker.broadcastToAll(message, "alice");

        assertThat(sentCount).isEqualTo(1);
        assertThat(session1.getSentMessages()).isEmpty();
        assertThat(session2.getSentMessages()).hasSize(1);
    }

    @Test
    @DisplayName("Должен корректно обработать рассылку без получателей")
    void testBroadcastWithNoRecipients() {
        MessageDto message = new MessageDto("1", "system", null, "Hello all!", MessageType.SYSTEM, null, false);
        int sentCount = messageBroker.broadcastToAll(message, null);

        assertThat(sentCount).isEqualTo(0);
    }

    @Test
    @DisplayName("Должен закрыть все сессии")
    void testCloseAllSessions() {
        messageBroker.registerSession("alice", session1);
        messageBroker.registerSession("bob", session2);

        messageBroker.closeAllSessions();

        assertThat(messageBroker.getActiveConnectionsCount()).isEqualTo(0);
        assertThat(session1.isOpen()).isFalse();
        assertThat(session2.isOpen()).isFalse();
    }

    @Test
    @DisplayName("Должен получить username по sessionId")
    void testGetUsernameBySessionId() {
        messageBroker.registerSession("alice", session1);

        String username = messageBroker.getUsernameBySessionId("session1");
        assertThat(username).isEqualTo("alice");
    }

    @Test
    @DisplayName("Должен вернуть null для несуществующей sessionId")
    void testGetUsernameByInvalidSessionId() {
        String username = messageBroker.getUsernameBySessionId("invalid");
        assertThat(username).isNull();
    }

    @Test
    @DisplayName("Должен выдержать множество одновременных подключений")
    void testConcurrentConnections() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    MockWebSocketSession session = new MockWebSocketSession("session" + index);
                    messageBroker.registerSession("user" + index, session);
                    successCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed).isTrue();
        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(messageBroker.getActiveConnectionsCount()).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("Должен выдержать множество одновременных сообщений")
    void testConcurrentMessages() throws InterruptedException {
        messageBroker.registerSession("alice", session1);
        messageBroker.registerSession("bob", session2);

        int messageCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(messageCount);
        AtomicInteger sentCount = new AtomicInteger(0);

        for (int i = 0; i < messageCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    MessageDto message = new MessageDto(
                        String.valueOf(index),
                        "alice",
                        null,
                        "Message " + index,
                        MessageType.TEXT,
                        null,
                        false
                    );
                    messageBroker.broadcastToAll(message, null);
                    sentCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed).isTrue();
        assertThat(sentCount.get()).isEqualTo(messageCount);
    }

    @Test
    @DisplayName("Должен корректно обработать повторную регистрацию пользователя")
    void testReRegisterSameUser() {
        messageBroker.registerSession("alice", session1);
        messageBroker.registerSession("alice", session2);

        // Старая сессия должна быть заменена новой
        assertThat(messageBroker.getActiveConnectionsCount()).isEqualTo(1);
        assertThat(messageBroker.isUserOnline("alice")).isTrue();
    }
}