package com.telegram.clone.client.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Утилиты для работы с темой.
 */
public final class ThemeUtils {

    private ThemeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Получение цвета из UI Manager.
     *
     * @param key ключ цвета
     * @return цвет
     */
    public static Color getUIColor(String key) {
        return UIManager.getColor(key);
    }

    /**
     * Установка цвета компонента.
     *
     * @param component компонент
     * @param background цвет фона
     * @param foreground цвет текста
     */
    public static void styleComponent(JComponent component, Color background, Color foreground) {
        component.setBackground(background);
        component.setForeground(foreground);
    }

    /**
     * Создание цветного значка.
     *
     * @param color цвет
     * @param size размер
     * @return иконка
     */
    public static Icon createColorIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(color);
                g2.fillOval(x, y, size, size);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
}