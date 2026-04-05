package com.telegram.clone.client.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Утилиты для Swing компонентов.
 */
public final class SwingUtils {

    private SwingUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Безопасное выполнение в EDT.
     *
     * @param runnable задача для выполнения
     */
    public static void invokeLater(Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Создание скругленной панели.
     *
     * @param radius радиус скругления
     * @return панель
     */
    public static JPanel createRoundedPanel(int radius) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
            }
        };
    }

    /**
     * Прокрутка компонента к видимой области.
     *
     * @param scrollPane панель прокрутки
     * @param component компонент
     */
    public static void scrollToVisible(JScrollPane scrollPane, JComponent component) {
        Rectangle rect = component.getBounds();
        component.scrollRectToVisible(rect);
        scrollPane.getViewport().scrollRectToVisible(rect);
    }

    /**
     * Ограничение размера текста.
     *
     * @param text текст
     * @param maxLength максимальная длина
     * @return ограниченный текст
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}