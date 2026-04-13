package com.homebakery.ui.auth;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class TopIconBadge extends JPanel {
    private final String emoji;
    private final java.awt.Color circleBg;

    TopIconBadge(String emoji, java.awt.Color circleBg) {
        this.emoji = emoji;
        this.circleBg = circleBg;
        setOpaque(false);
        Dimension d = new Dimension(72, 72);
        setPreferredSize(d);
        setMaximumSize(d);
        setAlignmentX(CENTER_ALIGNMENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int s = 56;
        int x = (getWidth() - s) / 2;
        int y = (getHeight() - s) / 2;
        g2.setColor(circleBg);
        g2.fillOval(x, y, s, s);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 26));
        var fm = g2.getFontMetrics();
        int tw = fm.stringWidth(emoji);
        int tx = x + (s - tw) / 2;
        int ty = y + (s + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(emoji, tx, ty);
        g2.dispose();
    }
}
