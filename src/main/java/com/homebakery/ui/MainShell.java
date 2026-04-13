package com.homebakery.ui;

import com.homebakery.service.BakeryDataService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

public final class MainShell extends JPanel {
    private static final int SIDEBAR_W = 248;

    private final CardLayout cards = new CardLayout();
    private final JPanel cardHost = new JPanel(cards);
    private final JButton dashNav = navButton("\u2630  Dashboard");
    private final JButton ordersNav = navButton("\uD83D\uDECD  Orders");
    private final JButton inventoryNav = navButton("\uD83D\uDCE6  Inventory");
    private final JButton recipesNav = navButton("\uD83C\uDF73  Recipes");
    private final SessionContext session;
    private final JLabel userNameLabel = new JLabel();
    private final JLabel roleLabel = new JLabel();

    public MainShell(BakeryDataService data, SessionContext session, Runnable onLogout) {
        super(new BorderLayout());
        this.session = session;
        setBackground(ShellTheme.WORKSPACE_BG);

        cardHost.setOpaque(false);
        cardHost.add(wrapWorkspace(new DashboardPanel(data)), "dashboard");
        cardHost.add(wrapWorkspace(new OrdersPane(data)), "orders");
        cardHost.add(wrapWorkspace(new InventoryPane(data)), "inventory");
        cardHost.add(wrapWorkspace(new RecipesPane(data)), "recipes");

        add(buildSidebar(onLogout), BorderLayout.WEST);
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBackground(ShellTheme.WORKSPACE_BG);
        center.add(cardHost, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        wireNav(dashNav, "dashboard");
        wireNav(ordersNav, "orders");
        wireNav(inventoryNav, "inventory");
        wireNav(recipesNav, "recipes");

        selectNav(dashNav);
        cards.show(cardHost, "dashboard");
    }

    private static JPanel wrapWorkspace(JPanel inner) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBackground(ShellTheme.WORKSPACE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        p.add(inner, BorderLayout.CENTER);
        return p;
    }

    public void refreshUserFooter() {
        userNameLabel.setText(session.getUsername());
        roleLabel.setText(session.getRoleDisplay());
    }

    private void wireNav(JButton b, String name) {
        b.addActionListener(e -> {
            cards.show(cardHost, name);
            selectNav(b);
        });
    }

    private void selectNav(JButton active) {
        for (JButton b : new JButton[] {dashNav, ordersNav, inventoryNav, recipesNav}) {
            styleNavPlain(b);
        }
        styleNavSelected(active);
    }

    private JPanel buildSidebar(Runnable onLogout) {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(ShellTheme.SIDEBAR_BG);
        side.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, ShellTheme.SIDEBAR_BORDER),
                BorderFactory.createEmptyBorder(20, 18, 20, 18)));
        side.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        side.setMinimumSize(new Dimension(SIDEBAR_W, 0));
        side.setMaximumSize(new Dimension(SIDEBAR_W, Integer.MAX_VALUE));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel hat = new JLabel("\uD83C\uDF73");
        hat.setFont(hat.getFont().deriveFont(22f));
        JPanel words = new JPanel(new BorderLayout(0, 2));
        words.setOpaque(false);
        JLabel title = new JLabel("Bakery System");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        title.setForeground(ShellTheme.TEXT_TITLE);
        JLabel subtitle = new JLabel("Order & Inventory");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        subtitle.setForeground(ShellTheme.TEXT_SUB);
        words.add(title, BorderLayout.NORTH);
        words.add(subtitle, BorderLayout.SOUTH);
        brand.add(hat);
        brand.add(words);

        Box nav = Box.createVerticalBox();
        nav.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (JButton b : new JButton[] {dashNav, ordersNav, inventoryNav, recipesNav}) {
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            nav.add(b);
            nav.add(Box.createVerticalStrut(4));
        }

        side.add(brand);
        side.add(Box.createVerticalStrut(20));
        side.add(nav);
        side.add(Box.createVerticalGlue());

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setForeground(ShellTheme.SIDEBAR_BORDER);
        side.add(sep);
        side.add(Box.createVerticalStrut(14));

        JPanel profile = new JPanel();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.setOpaque(false);
        profile.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatar = new JLabel("\uD83D\uDC64");
        avatar.setFont(avatar.getFont().deriveFont(28f));
        avatar.setAlignmentX(Component.LEFT_ALIGNMENT);

        userNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        userNameLabel.setForeground(ShellTheme.TEXT_TITLE);
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        roleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        roleLabel.setForeground(ShellTheme.TEXT_SUB);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logout = new JButton("\uD83D\uDEAA  Logout");
        logout.setFont(ShellTheme.NAV);
        logout.setForeground(ShellTheme.LOGOUT_RED);
        logout.setContentAreaFilled(false);
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.addActionListener(e -> onLogout.run());

        profile.add(avatar);
        profile.add(Box.createVerticalStrut(6));
        profile.add(userNameLabel);
        profile.add(roleLabel);
        profile.add(Box.createVerticalStrut(10));
        profile.add(logout);

        side.add(profile);

        return side;
    }

    private static JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFont(ShellTheme.NAV);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        b.setFocusPainted(false);
        return b;
    }

    private static void styleNavSelected(JButton b) {
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBackground(ShellTheme.NAV_ACTIVE_BG);
        b.setForeground(ShellTheme.PRIMARY_ORANGE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 12, 10, 12),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    }

    private static void styleNavPlain(JButton b) {
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setForeground(ShellTheme.TEXT_TITLE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    }
}
