package com.telegram.clone.client;

import com.formdev.flatlaf.FlatDarkLaf;
import com.telegram.clone.client.view.LoginDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * Главный класс клиентского приложения.
 * Запускает Swing приложение с темной темой Telegram.
 */
@Slf4j
public class ClientApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // Настройка темной темы FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            log.error("Failed to set FlatDarkLaf", e);
        }

        // Запуск UI в Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog();
            loginDialog.setVisible(true);
        });
    }
}