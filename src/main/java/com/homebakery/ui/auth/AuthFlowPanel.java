package com.homebakery.ui.auth;

import com.homebakery.service.AuthService;
import com.homebakery.service.BakeryDataService;
import com.homebakery.ui.MainShell;
import com.homebakery.ui.SessionContext;

import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * Switches between login, sign-up, and the main bakery workspace.
 */
public final class AuthFlowPanel extends JPanel {
    private static final String CARD_LOGIN = "login";
    private static final String CARD_SIGNUP = "signup";
    private static final String CARD_MAIN = "main";

    private final CardLayout cards = new CardLayout();
    private final SessionContext session = new SessionContext();
    private final MainShell mainShell;

    public AuthFlowPanel(BakeryDataService data, AuthService auth) {
        setLayout(cards);
        setBackground(AuthTheme.PAGE_BG);

        mainShell = new MainShell(data, session, this::logout);

        add(new LoginPanel(this, auth), CARD_LOGIN);
        add(new SignUpPanel(this, auth), CARD_SIGNUP);
        add(mainShell, CARD_MAIN);

        cards.show(this, CARD_LOGIN);
    }

    void showLogin() {
        cards.show(this, CARD_LOGIN);
    }

    void showSignUp() {
        cards.show(this, CARD_SIGNUP);
    }

    void enterMainWorkspace(String username, AuthService auth) {
        session.setUsername(username.trim());
        if (username.trim().equalsIgnoreCase("admin")) {
            session.setRoleDisplay("Admin");
        } else {
            session.setRoleDisplay(auth.roleFor(username));
        }
        mainShell.refreshUserFooter();
        cards.show(this, CARD_MAIN);
    }

    private void logout() {
        session.setUsername("");
        session.setRoleDisplay("");
        mainShell.refreshUserFooter();
        cards.show(this, CARD_LOGIN);
    }
}
