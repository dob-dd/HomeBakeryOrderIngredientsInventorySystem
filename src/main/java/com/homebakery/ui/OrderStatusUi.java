package com.homebakery.ui;

import com.homebakery.model.OrderStatus;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/** Maps order workflow to dashboard-style status labels and badge colors. */
public final class OrderStatusUi {
    private OrderStatusUi() {
    }

    public static String label(OrderStatus s) {
        return switch (s) {
            case NEW -> "Pending";
            case IN_PROGRESS -> "Baking";
            case READY -> "Ready";
            case COMPLETED -> "Fulfilled";
            case CANCELLED -> "Cancelled";
        };
    }

    public static Color badgeBackground(OrderStatus s) {
        return switch (s) {
            case NEW -> new Color(0xff, 0xf4, 0xcc);
            case IN_PROGRESS -> new Color(0xd9, 0xed, 0xff);
            case READY -> new Color(0xe0, 0xf2, 0xfe);
            case COMPLETED -> new Color(0xd1, 0xf4, 0xe0);
            case CANCELLED -> new Color(0xee, 0xee, 0xee);
        };
    }

    public static Color badgeForeground(OrderStatus s) {
        return switch (s) {
            case NEW -> new Color(0x8a, 0x6d, 0x0b);
            case IN_PROGRESS -> new Color(0x1, 0x56, 0x9c);
            case READY -> new Color(0x2, 0x6c, 0x9c);
            case COMPLETED -> new Color(0x1, 0x6c, 0x3a);
            case CANCELLED -> new Color(0x55, 0x55, 0x55);
        };
    }

    public static DefaultTableCellRenderer badgeRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(JLabel.CENTER);
                if (value instanceof OrderStatus s) {
                    l.setText(label(s));
                    if (!isSelected) {
                        l.setBackground(badgeBackground(s));
                        l.setForeground(badgeForeground(s));
                    }
                    Border b = BorderFactory.createEmptyBorder(4, 10, 4, 10);
                    l.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1), b));
                }
                l.setOpaque(true);
                return l;
            }
        };
    }
}
