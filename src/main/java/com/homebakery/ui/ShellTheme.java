package com.homebakery.ui;

import java.awt.Color;
import java.awt.Font;

/** Light shell aligned with Bakery System dashboard mockups. */
public final class ShellTheme {
    public static final Color SIDEBAR_BG = Color.WHITE;
    public static final Color SIDEBAR_BORDER = new Color(0xea, 0xec, 0xf0);
    public static final Color WORKSPACE_BG = new Color(0xf8, 0xf9, 0xfa);
    public static final Color CARD_WHITE = Color.WHITE;
    public static final Color TEXT_TITLE = new Color(0x1a, 0x1a, 0x1a);
    public static final Color TEXT_SUB = new Color(0x66, 0x66, 0x66);
    public static final Color NAV_ACTIVE_BG = new Color(0xff, 0xf3, 0xe6);
    public static final Color PRIMARY_ORANGE = new Color(0xe6, 0x7e, 0x00);
    public static final Color LOGOUT_RED = new Color(0xdc, 0x26, 0x26);
    public static final Font TITLE_LG = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    public static final Font TITLE_MD = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public static final Font BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font NAV = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    private ShellTheme() {
    }
}
