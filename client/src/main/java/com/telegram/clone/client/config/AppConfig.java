package com.telegram.clone.client.config;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Конфигурация клиентского приложения.
 */
public final class AppConfig {

    private AppConfig() {
        throw new UnsupportedOperationException("Config class");
    }

    /**
     * Настройка темы приложения.
     */
    public static void setupTheme() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());

            // Дополнительные настройки UI
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);

            UIManager.put("ScrollBar.thumbArc", 10);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

            UIManager.put("ToolBar.arc", 10);
            UIManager.put("PopupMenu.arc", 10);

        } catch (Exception e) {
            System.err.println("Failed to set FlatDarkLaf: " + e.getMessage());
        }
    }

    /**
     * Настройка глобальных шрифтов.
     *
     * @param fontName имя шрифта
     * @param size размер шрифта
     */
    public static void setupFonts(String fontName, int size) {
        Font font = new Font(fontName, Font.PLAIN, size);
        UIManager.put("defaultFont", font);
    }
}