package com.homebakery.ui.auth;

import com.homebakery.service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class LoginPanel extends JPanel {
    public LoginPanel(AuthFlowPanel flow, AuthService auth) {
        super(new GridBagLayout());
        setBackground(AuthTheme.PAGE_BG);

        AuthCardPanel card = new AuthCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(28, 36, 32, 36));

        TopIconBadge badge = new TopIconBadge("\uD83D\uDD12", AuthTheme.ICON_CIRCLE_LOGIN);
        badge.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(badge);
        inner.add(Box.createVerticalStrut(8));

        JLabel title = new JLabel("Welcome, User");
        title.setFont(AuthTheme.HEADING);
        title.setForeground(AuthTheme.TEXT_DARK);
        title.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(title);
        inner.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("Bakery Order & Inventory System");
        subtitle.setFont(AuthTheme.SUBHEAD);
        subtitle.setForeground(AuthTheme.TEXT_GRAY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(subtitle);
        inner.add(Box.createVerticalStrut(28));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(AuthTheme.LABEL);
        userLabel.setForeground(AuthTheme.TEXT_GRAY);
        userLabel.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(userLabel);
        inner.add(Box.createVerticalStrut(6));

        HintTextField userField = new HintTextField("Enter your username");
        IconInputRow userRow = IconInputRow.text("\uD83D\uDC64", userField);
        userRow.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(userRow);
        inner.add(Box.createVerticalStrut(16));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(AuthTheme.LABEL);
        passLabel.setForeground(AuthTheme.TEXT_GRAY);
        passLabel.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(passLabel);
        inner.add(Box.createVerticalStrut(6));

        HintPasswordField passField = new HintPasswordField("Enter your password");
        IconInputRow passRow = IconInputRow.password("\uD83D\uDD12", passField);
        passRow.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(passRow);
        inner.add(Box.createVerticalStrut(24));

        OrangeButton signIn = new OrangeButton("Sign In");
        signIn.setAlignmentX(CENTER_ALIGNMENT);
        signIn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter username and password.",
                        "Sign in",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!auth.login(u, p)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Sign in",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            flow.enterMainWorkspace(u, auth);
        });
        inner.add(signIn);
        inner.add(Box.createVerticalStrut(20));

        JPanel demo = new JPanel();
        demo.setLayout(new BoxLayout(demo, BoxLayout.Y_AXIS));
        demo.setOpaque(true);
        demo.setBackground(AuthTheme.DEMO_BOX_BG);
        demo.setBorder(
                BorderFactory.createCompoundBorder(
                        RoundedCorners.outline(new Color(0xdd, 0xdd, 0xdd), 10, 1),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        demo.setAlignmentX(CENTER_ALIGNMENT);
        JLabel demoTitle = new JLabel("Demo Accounts:");
        demoTitle.setFont(AuthTheme.LABEL);
        demoTitle.setForeground(AuthTheme.TEXT_DARK);
        demo.add(demoTitle);
        demo.add(Box.createVerticalStrut(6));
        JLabel lines = new JLabel(
                "<html><body style='font-family:monospace;font-size:11px;color:#444'>"
                        + "• admin / admin123<br/>"
                        + "• baker / baker123<br/>"
                        + "• manager / manager123</body></html>");
        demo.add(lines);
        inner.add(demo);
        inner.add(Box.createVerticalStrut(20));

        JLabel footer = new JLabel(
                "<html><div style='text-align:center'>Don't have an account? "
                        + "<font color='#E67E00'><b>Sign up</b></font></div></html>");
        footer.setFont(AuthTheme.BODY);
        footer.setAlignmentX(CENTER_ALIGNMENT);
        footer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        footer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                flow.showSignUp();
            }
        });
        inner.add(footer);

        card.add(inner);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(24, 24, 24, 24);
        c.anchor = GridBagConstraints.CENTER;
        add(card, c);

        card.setPreferredSize(new Dimension(420, 640));
    }
}
