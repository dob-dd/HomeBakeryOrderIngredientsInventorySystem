package com.homebakery.ui.auth;

import com.homebakery.service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class SignUpPanel extends JPanel {
    public SignUpPanel(AuthFlowPanel flow, AuthService auth) {
        super(new GridBagLayout());
        setBackground(AuthTheme.PAGE_BG);

        AuthCardPanel card = new AuthCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        TopIconBadge badge = new TopIconBadge("\uD83D\uDC64", AuthTheme.ICON_CIRCLE_SIGNUP);
        badge.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(badge);
        inner.add(Box.createVerticalStrut(8));

        JLabel title = new JLabel("Create Account");
        title.setFont(AuthTheme.HEADING);
        title.setForeground(AuthTheme.TEXT_DARK);
        title.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(title);
        inner.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("Join the Bakery System");
        subtitle.setFont(AuthTheme.SUBHEAD);
        subtitle.setForeground(AuthTheme.TEXT_GRAY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(subtitle);
        inner.add(Box.createVerticalStrut(22));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(AuthTheme.LABEL);
        userLabel.setForeground(AuthTheme.TEXT_GRAY);
        userLabel.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(userLabel);
        inner.add(Box.createVerticalStrut(6));

        HintTextField userField = new HintTextField("Choose a username");
        IconInputRow userRow = IconInputRow.text("\uD83D\uDC64", userField);
        userRow.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(userRow);
        inner.add(Box.createVerticalStrut(12));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(AuthTheme.LABEL);
        passLabel.setForeground(AuthTheme.TEXT_GRAY);
        passLabel.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(passLabel);
        inner.add(Box.createVerticalStrut(6));

        HintPasswordField passField = new HintPasswordField("Create a password");
        IconInputRow passRow = IconInputRow.password("\uD83D\uDD12", passField);
        passRow.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(passRow);
        inner.add(Box.createVerticalStrut(12));

        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(AuthTheme.LABEL);
        confirmLabel.setForeground(AuthTheme.TEXT_GRAY);
        confirmLabel.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(confirmLabel);
        inner.add(Box.createVerticalStrut(6));

        HintPasswordField confirmField = new HintPasswordField("Confirm your password");
        IconInputRow confirmRow = IconInputRow.password("\uD83D\uDD12", confirmField);
        confirmRow.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(confirmRow);
        inner.add(Box.createVerticalStrut(12));

        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(AuthTheme.LABEL);
        roleLabel.setForeground(AuthTheme.TEXT_GRAY);
        roleLabel.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(roleLabel);
        inner.add(Box.createVerticalStrut(6));

        JComboBox<String> roleBox = new JComboBox<>(new String[] {"Baker", "Manager"});
        roleBox.setSelectedItem("Baker");
        IconComboRow roleRow = new IconComboRow("\uD83D\uDECD", roleBox);
        roleRow.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(roleRow);
        inner.add(Box.createVerticalStrut(22));

        OrangeButton create = new OrangeButton("Create Account");
        create.setAlignmentX(CENTER_ALIGNMENT);
        create.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            String c = new String(confirmField.getPassword());
            String role = (String) roleBox.getSelectedItem();
            if (role == null) {
                role = "Baker";
            }
            String err = auth.register(u, p, c, role);
            if (err != null) {
                JOptionPane.showMessageDialog(this, err, "Create account", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Account created. You can sign in now.",
                    "Create account",
                    JOptionPane.INFORMATION_MESSAGE);
            flow.showLogin();
        });
        inner.add(create);
        inner.add(Box.createVerticalStrut(18));

        JLabel footer = new JLabel(
                "<html><div style='text-align:center'>Already have an account? "
                        + "<font color='#E67E00'><b>Sign in</b></font></div></html>");
        footer.setFont(AuthTheme.BODY);
        footer.setAlignmentX(CENTER_ALIGNMENT);
        footer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        footer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                flow.showLogin();
            }
        });
        inner.add(footer);

        card.add(inner);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(24, 24, 24, 24);
        gc.anchor = GridBagConstraints.CENTER;
        add(card, gc);

        card.setPreferredSize(new Dimension(420, 720));
    }
}
