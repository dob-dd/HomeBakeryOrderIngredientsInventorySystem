package com.homebakery.ui;

import com.homebakery.model.BakeryOrder;
import com.homebakery.model.OrderLine;
import com.homebakery.model.OrderStatus;
import com.homebakery.service.BakeryDataService;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;

public final class DashboardPanel extends JPanel {
    private final BakeryDataService data;
    private final NumberFormat money = BakeryFormat.peso();
    private final RecentModel recentModel = new RecentModel();
    private final JTable recentTable = new JTable(recentModel);

    public DashboardPanel(BakeryDataService data) {
        super(new BorderLayout());
        this.data = data;
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel head = new JLabel("Dashboard");
        head.setFont(ShellTheme.TITLE_LG);
        head.setForeground(ShellTheme.TEXT_TITLE);
        JLabel sub = new JLabel("Overview of your bakery operations");
        sub.setFont(ShellTheme.BODY);
        sub.setForeground(ShellTheme.TEXT_SUB);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titles = new JPanel(new BorderLayout(0, 4));
        titles.setOpaque(false);
        titles.add(head, BorderLayout.NORTH);
        titles.add(sub, BorderLayout.SOUTH);
        header.add(titles, BorderLayout.WEST);

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(0, 0, 16, 0));

        Runnable refreshStats = () -> {
            stats.removeAll();
            stats.add(statCard("\uD83D\uDCCB", "Pending Orders", String.valueOf(data.pendingOrderCount()), new Color(0x3b, 0x82, 0xf6)));
            stats.add(statCard("\u23F3", "In Progress", String.valueOf(data.inProgressOrderCount()), ShellTheme.PRIMARY_ORANGE));
            stats.add(statCard("\u26A0", "Low Stock Alerts", String.valueOf(data.lowStockCount()), new Color(0xef, 0x44, 0x44)));
            stats.add(statCard("\u20B1", "Total Revenue", money.format(data.totalRevenue()), new Color(0x22, 0xc5, 0x5e)));
            stats.revalidate();
            stats.repaint();
        };

        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(stats, BorderLayout.SOUTH);

        recentTable.setRowHeight(32);
        recentTable.setShowGrid(false);
        recentTable.setFillsViewportHeight(true);
        styleTableHeader(recentTable);
        recentTable.getColumnModel().getColumn(3).setCellRenderer(OrderStatusUi.badgeRenderer());
        recentTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Number n) {
                    l.setText(money.format(n.doubleValue()));
                }
                l.setHorizontalAlignment(JLabel.RIGHT);
                return l;
            }
        });

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(ShellTheme.CARD_WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xea, 0xec, 0xf0)),
                new EmptyBorder(16, 18, 16, 18)));
        JLabel recentTitle = new JLabel("Recent Orders");
        recentTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        recentTitle.setForeground(ShellTheme.TEXT_TITLE);
        recentTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        tableCard.add(recentTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(recentTable), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);

        data.addChangeListener(() -> SwingUtilities.invokeLater(() -> {
            refreshStats.run();
            recentModel.refresh();
        }));

        refreshStats.run();
        recentModel.refresh();
    }

    private JPanel statCard(String icon, String label, String value, Color iconTint) {
        JPanel card = new JPanel(new BorderLayout(12, 8));
        card.setBackground(ShellTheme.CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xea, 0xec, 0xf0)),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel ic = new JLabel(icon);
        ic.setFont(ic.getFont().deriveFont(22f));
        ic.setForeground(iconTint);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lbl.setForeground(ShellTheme.TEXT_SUB);

        JLabel val = new JLabel(value);
        val.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        val.setForeground(ShellTheme.TEXT_TITLE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(ic);

        JPanel text = new JPanel(new BorderLayout());
        text.setOpaque(false);
        text.add(lbl, BorderLayout.NORTH);
        text.add(val, BorderLayout.CENTER);

        card.add(left, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private static void styleTableHeader(JTable t) {
        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(0xf9, 0xfa, 0xfb));
        h.setForeground(new Color(0x6b, 0x72, 0x80));
        h.setFont(h.getFont().deriveFont(Font.BOLD, 11f));
        h.setPreferredSize(new Dimension(0, 36));
    }

    private final class RecentModel extends AbstractTableModel {
        private final String[] cols = {"CUSTOMER", "PRODUCT", "QUANTITY", "STATUS", "TOTAL"};
        private List<BakeryOrder> rows = List.of();

        void refresh() {
            rows = data.getOrders().stream()
                    .sorted(Comparator.comparing(BakeryOrder::getDueDate).reversed())
                    .limit(12)
                    .toList();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int column) {
            return cols[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BakeryOrder o = rows.get(rowIndex);
            OrderLine first = o.getLines().isEmpty() ? null : o.getLines().get(0);
            return switch (columnIndex) {
                case 0 -> o.getCustomerName();
                case 1 -> first == null ? "—" : first.getProductName();
                case 2 -> first == null ? 0 : first.getQuantity();
                case 3 -> o.getStatus();
                case 4 -> o.subtotal();
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 2 -> Integer.class;
                case 3 -> OrderStatus.class;
                case 4 -> Double.class;
                default -> String.class;
            };
        }
    }
}
