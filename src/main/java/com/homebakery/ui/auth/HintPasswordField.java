package com.homebakery.ui.auth;

import javax.swing.JPasswordField;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class HintPasswordField extends JPasswordField {
    private final String hint;

    HintPasswordField(String hint) {
        this.hint = hint;
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 4, 8, 8));
        setForeground(AuthTheme.TEXT_DARK);
        setFont(AuthTheme.BODY);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getPassword().length == 0 && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(AuthTheme.TEXT_MUTED);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            var fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(hint, x, y);
            g2.dispose();
        }
    }
}
