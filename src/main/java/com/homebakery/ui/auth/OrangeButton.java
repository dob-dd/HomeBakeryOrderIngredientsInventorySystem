package com.homebakery.ui.auth;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class OrangeButton extends JButton {
    OrangeButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(AuthTheme.BUTTON);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(320, 48));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = AuthTheme.PRIMARY_ORANGE;
        if (getModel().isPressed()) {
            fill = AuthTheme.PRIMARY_ORANGE.darker();
        } else if (getModel().isRollover()) {
            fill = AuthTheme.PRIMARY_ORANGE_HOVER;
        }
        g2.setColor(fill);
        int r = RoundedCorners.RADIUS_BUTTON;
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);

        String t = getText();
        g2.setColor(Color.WHITE);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int tx = (getWidth() - fm.stringWidth(t)) / 2;
        int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(t, tx, ty);
        g2.dispose();
    }
}
