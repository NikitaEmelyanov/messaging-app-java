package com.telegram.clone.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Конфигурация пулов потоков для сервера.
 * Оптимизирует обработку WebSocket сообщений и фоновых задач.
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * Создает Executor для асинхронной обработки сообщений.
     *
     * @return настроенный ThreadPoolTaskExecutor
     */
    @Bean(name = "messageExecutor")
    public Executor messageExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("msg-processor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    /**
     * Создает Executor для обработки подключений пользователей.
     *
     * @return настроенный ThreadPoolTaskExecutor
     */
    @Bean(name = "connectionExecutor")
    public Executor connectionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("conn-handler-");
        executor.initialize();
        return executor;
    }

    /**
     * Создает планировщик для фоновых задач (очистка сессий, heartbeat).
     *
     * @return настроенный ThreadPoolTaskScheduler
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();
        return scheduler;
    }
}