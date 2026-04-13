package com.homebakery.ui;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/**
 * Bakery palette aligned with the prior JavaFX {@code styles.css} (warm cream + espresso sidebar).
 */
public final class UiTheme {
    public static final Color BG_CREAM = new Color(0xf6, 0xf1, 0xea);
    public static final Color SIDEBAR_BG = new Color(0x3d, 0x2b, 0x1f);
    public static final Color SIDEBAR_TEXT = new Color(0xf6, 0xf1, 0xea);
    public static final Color SIDEBAR_MUTED = new Color(0xbf, 0xb5, 0xa8);
    public static final Color ACCENT = new Color(0xc5, 0x8b, 0x5c);
    public static final Color ACCENT_TEXT = new Color(0x1f, 0x14, 0x0d);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(0x2a, 0x1d, 0x15);
    public static final Color TEXT_MUTED = new Color(0x6b, 0x5a, 0x4d);
    public static final Color DANGER_BG = new Color(0xf0, 0xde, 0xd8);
    public static final Color DANGER_FG = new Color(0x7a, 0x2f, 0x24);
    public static final Color SECONDARY_BG = new Color(0xec, 0xe4, 0xda);
    public static final Color TABLE_HEADER = new Color(0xf0, 0xe8, 0xdf);
    public static final Color LOW_STOCK_ROW = new Color(255, 245, 240);
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font SIDEBAR_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    public static final Font BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);

    private UiTheme() {
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("Table.alternateRowColor", new Color(0xfa, 0xf7, 0xf3));
        UIManager.put("Table.selectionBackground", new Color(197, 139, 92));
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xdd, 0xd4, 0xca)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18));
    }

    public static Border padded(int top, int left, int bottom, int right) {
        return BorderFactory.createEmptyBorder(top, left, bottom, right);
    }
}
