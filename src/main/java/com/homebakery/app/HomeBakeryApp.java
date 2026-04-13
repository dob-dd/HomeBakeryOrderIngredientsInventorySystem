package com.homebakery.app;

import com.homebakery.service.AuthService;
import com.homebakery.service.BakeryDataService;
import com.homebakery.ui.AppFrame;
import com.homebakery.ui.UiTheme;
import com.homebakery.ui.auth.AuthFlowPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
public final class HomeBakeryApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiTheme.install();
            BakeryDataService data = new BakeryDataService();
            AuthService auth = new AuthService();

            JFrame frame = new JFrame("Home Bakery Order & Ingredients Inventory System");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new AuthFlowPanel(data, auth));
            frame.setMinimumSize(AppFrame.MIN_SIZE);
            frame.setSize(AppFrame.SIZE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
