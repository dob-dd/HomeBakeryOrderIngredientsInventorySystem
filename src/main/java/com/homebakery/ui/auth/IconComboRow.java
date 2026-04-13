package com.homebakery.ui.auth;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

final class IconComboRow extends JPanel {
    IconComboRow(String iconGlyph, JComboBox<String> combo) {
        super(new BorderLayout(8, 0));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(RoundedCorners.outline(AuthTheme.FIELD_BORDER, RoundedCorners.RADIUS_FIELD, 1));

        JLabel icon = new JLabel(iconGlyph);
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        icon.setForeground(AuthTheme.TEXT_GRAY);
        icon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(icon, BorderLayout.WEST);

        combo.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 4, 6, 8));
        combo.setBackground(Color.WHITE);
        combo.setFont(AuthTheme.BODY);
        combo.setForeground(AuthTheme.TEXT_DARK);
        add(combo, BorderLayout.CENTER);

        setPreferredSize(new Dimension(320, 44));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }
}
