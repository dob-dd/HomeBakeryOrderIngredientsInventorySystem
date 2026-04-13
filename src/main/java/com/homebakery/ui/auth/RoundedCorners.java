package com.homebakery.ui.auth;

import javax.swing.border.AbstractBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

public final class RoundedCorners {
    public static final int RADIUS_FIELD = 10;
    public static final int RADIUS_CARD = 18;
    public static final int RADIUS_BUTTON = 10;

    private RoundedCorners() {
    }

    public static AbstractBorder outline(Color color, int radius, int thickness) {
        return new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(thickness));
                int t = thickness;
                g2.drawRoundRect(x + t / 2, y + t / 2, width - t, height - t, radius, radius);
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(10, 12, 10, 12);
            }
        };
    }
}
