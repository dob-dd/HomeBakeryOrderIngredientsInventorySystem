package com.homebakery.ui.auth;

import java.awt.Color;
import java.awt.Font;

/** Colors aligned with Figma-style login / sign-up mockups. */
public final class AuthTheme {
    public static final Color PAGE_BG = new Color(0xff, 0xf8, 0xe7);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color PRIMARY_ORANGE = new Color(0xe6, 0x7e, 0x00);
    public static final Color PRIMARY_ORANGE_HOVER = new Color(0xf0, 0x8c, 0x0a);
    public static final Color TEXT_DARK = new Color(0x1a, 0x1a, 0x1a);
    public static final Color TEXT_GRAY = new Color(0x66, 0x66, 0x66);
    public static final Color TEXT_MUTED = new Color(0x88, 0x88, 0x88);
    public static final Color FIELD_BORDER = new Color(0xdd, 0xdd, 0xdd);
    public static final Color DEMO_BOX_BG = new Color(0xf5, 0xf5, 0xf5);
    public static final Color ICON_CIRCLE_LOGIN = new Color(0xff, 0xf3, 0xe0);
    public static final Color ICON_CIRCLE_SIGNUP = new Color(0xff, 0xef, 0xd5);

    public static final Font HEADING = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    public static final Font SUBHEAD = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font LABEL = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    public static final Font BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font BUTTON = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    public static final Font DEMO = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private AuthTheme() {
    }
}
