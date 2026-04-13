package com.homebakery.ui.auth;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/** Rounded bordered row with a leading icon glyph and a text/password field. */
final class IconInputRow extends JPanel {
    private IconInputRow(String iconGlyph, JTextField field) {
        super(new BorderLayout(8, 0));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(RoundedCorners.outline(AuthTheme.FIELD_BORDER, RoundedCorners.RADIUS_FIELD, 1));

        JLabel icon = new JLabel(iconGlyph);
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        icon.setForeground(AuthTheme.TEXT_GRAY);
        icon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(icon, BorderLayout.WEST);
        add(field, BorderLayout.CENTER);
        setPreferredSize(new Dimension(320, 44));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }

    static IconInputRow text(String icon, HintTextField field) {
        return new IconInputRow(icon, field);
    }

    static IconInputRow password(String icon, HintPasswordField field) {
        return new IconInputRow(icon, field);
    }
}
