package com.homebakery.ui.auth;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** White rounded card with a soft drop shadow (Figma-style). */
public final class AuthCardPanel extends JPanel {
    private static final int R = RoundedCorners.RADIUS_CARD;
    private static final int SHADOW = 6;

    public AuthCardPanel() {
        setOpaque(false);
        setBackground(AuthTheme.CARD_BG);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        for (int i = SHADOW; i >= 1; i--) {
            float alpha = 0.04f + (SHADOW - i) * 0.012f;
            g2.setColor(new Color(0, 0, 0, Math.min(alpha, 0.18f)));
            g2.fillRoundRect(i, i + 2, w - 2 * i, h - 2 * i - 2, R, R);
        }

        g2.setColor(AuthTheme.CARD_BG);
        g2.fillRoundRect(0, 0, w - SHADOW, h - SHADOW - 2, R, R);

        g2.setColor(new Color(0xe8, 0xe8, 0xe8));
        g2.drawRoundRect(0, 0, w - SHADOW - 1, h - SHADOW - 3, R, R);

        g2.dispose();
        super.paintComponent(g);
    }
}
