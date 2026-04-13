package com.homebakery.ui;

import com.homebakery.model.Ingredient;
import com.homebakery.service.BakeryDataService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;

public final class InventoryPane extends JPanel {
    private final BakeryDataService data;
    private final IngredientTableModel tableModel;
    private final JTable table;
    private final NumberFormat qtyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
    private final NumberFormat money = BakeryFormat.peso();
    private final JLabel lowStockLabel = new JLabel();
    private final JButton removeBtn = new JButton("Remove");

    public InventoryPane(BakeryDataService data) {
        super(new BorderLayout());
        this.data = data;
        setOpaque(false);
        qtyFormat.setMaximumFractionDigits(2);
        qtyFormat.setMinimumFractionDigits(0);

        tableModel = new IngredientTableModel(data);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.getTableHeader().setBackground(UiTheme.TABLE_HEADER);
        table.getTableHeader().setFont(table.getFont().deriveFont(java.awt.Font.BOLD));

        TableCellRenderer rowAware = new IngredientRowRenderer(tableModel, qtyFormat, money);
        table.setDefaultRenderer(String.class, rowAware);
        table.setDefaultRenderer(Double.class, rowAware);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeBtn.setEnabled(table.getSelectedRow() >= 0);
            }
        });

        JLabel title = new JLabel("Inventory");
        title.setFont(ShellTheme.TITLE_LG);
        title.setForeground(ShellTheme.TEXT_TITLE);
        JLabel subtitle = new JLabel("Manage ingredient stock and pricing");
        subtitle.setForeground(ShellTheme.TEXT_SUB);

        styleChip(lowStockLabel);
        refreshLowStockLabel();

        JButton add = new JButton("+ Add Ingredient");
        add.setBackground(ShellTheme.PRIMARY_ORANGE);
        add.setForeground(Color.WHITE);
        add.setFont(add.getFont().deriveFont(java.awt.Font.BOLD));
        add.setOpaque(true);
        add.setContentAreaFilled(true);
        add.setFocusPainted(false);
        add.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        add.addActionListener(e -> showAddDialog());

        JButton receive = new JButton("Receive stock");
        styleSecondary(receive);
        receive.addActionListener(e -> showReceiveDialog());

        removeBtn.setEnabled(false);
        styleDanger(removeBtn);
        removeBtn.addActionListener(e -> {
            int vRow = table.getSelectedRow();
            if (vRow < 0) {
                return;
            }
            Ingredient sel = tableModel.getIngredientAt(table.convertRowIndexToModel(vRow));
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "Remove ingredient \"" + sel.getName() + "\"?",
                    "Ingredients",
                    JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                data.removeIngredient(sel);
            }
        });

        JButton adjustQty = new JButton("Adjust on-hand…");
        styleSecondary(adjustQty);
        adjustQty.addActionListener(e -> showAdjustQuantityDialog());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(subtitle, BorderLayout.SOUTH);
        header.add(left, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(lowStockLabel);
        actions.add(add);
        actions.add(receive);
        actions.add(removeBtn);
        header.add(actions, BorderLayout.EAST);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tools.setOpaque(false);
        tools.add(adjustQty);

        JScrollPane scroll = new JScrollPane(table);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(tools, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(ShellTheme.CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xea, 0xec, 0xf0)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        card.add(header, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        data.addChangeListener(() -> SwingUtilities.invokeLater(() -> {
            tableModel.fireTableDataChanged();
            refreshLowStockLabel();
        }));
    }

    private void styleChip(JLabel l) {
        l.setOpaque(true);
        l.setFont(l.getFont().deriveFont(java.awt.Font.BOLD, 11f));
        l.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    }

    private void refreshLowStockLabel() {
        int n = data.lowStockCount();
        if (n == 0) {
            lowStockLabel.setText("All ingredients above minimum");
            lowStockLabel.setForeground(UiTheme.TEXT_MUTED);
            lowStockLabel.setBackground(new Color(0xec, 0xe4, 0xda));
        } else {
            lowStockLabel.setText(n + " ingredient(s) at or below minimum");
            lowStockLabel.setForeground(UiTheme.DANGER_FG);
            lowStockLabel.setBackground(new Color(214, 90, 61, 46));
        }
    }

    private static void styleSecondary(JButton b) {
        b.setBackground(UiTheme.SECONDARY_BG);
        b.setForeground(UiTheme.TEXT_PRIMARY);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    private static void styleDanger(JButton b) {
        b.setBackground(UiTheme.DANGER_BG);
        b.setForeground(UiTheme.DANGER_FG);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    private void showAdjustQuantityDialog() {
        int vRow = table.getSelectedRow();
        if (vRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an ingredient first.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Ingredient sel = tableModel.getIngredientAt(table.convertRowIndexToModel(vRow));
        JTextField amount = new JTextField(qtyFormat.format(sel.getQuantityOnHand()), 14);
        int opt = JOptionPane.showConfirmDialog(
                this,
                new Object[] {"Sets on-hand to this value (" + sel.getUnit() + ").", amount},
                "Adjust on-hand — " + sel.getName(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) {
            return;
        }
        double a;
        try {
            a = Double.parseDouble(amount.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a numeric quantity.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (a < 0) {
            JOptionPane.showMessageDialog(this, "Quantity cannot be negative.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int mRow = table.convertRowIndexToModel(vRow);
        sel.setQuantityOnHand(a);
        tableModel.fireTableRowsUpdated(mRow, mRow);
        refreshLowStockLabel();
    }

    private void showAddDialog() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        JTextField name = new JTextField(18);
        JTextField unit = new JTextField(18);
        JTextField qty = new JTextField("0", 10);
        JTextField min = new JTextField("0", 10);
        JTextField price = new JTextField("0.00", 10);
        g.gridx = 0;
        g.gridy = 0;
        p.add(new JLabel("Name"), g);
        g.gridx = 1;
        p.add(name, g);
        g.gridx = 0;
        g.gridy++;
        p.add(new JLabel("Unit"), g);
        g.gridx = 1;
        p.add(unit, g);
        g.gridx = 0;
        g.gridy++;
        p.add(new JLabel("Starting quantity"), g);
        g.gridx = 1;
        p.add(qty, g);
        g.gridx = 0;
        g.gridy++;
        p.add(new JLabel("Minimum level"), g);
        g.gridx = 1;
        p.add(min, g);
        g.gridx = 0;
        g.gridy++;
        p.add(new JLabel("Unit price"), g);
        g.gridx = 1;
        p.add(price, g);

        int opt = JOptionPane.showConfirmDialog(
                this, p, "Add ingredient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) {
            return;
        }
        String n = name.getText() == null ? "" : name.getText().trim();
        String u = unit.getText() == null ? "" : unit.getText().trim();
        if (n.isEmpty() || u.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and unit are required.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        double q;
        double m;
        double up;
        try {
            q = Double.parseDouble(qty.getText().trim().replace(",", ""));
            m = Double.parseDouble(min.getText().trim().replace(",", ""));
            up = Double.parseDouble(price.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantities and price must be numbers.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (q < 0 || m < 0 || up < 0) {
            JOptionPane.showMessageDialog(this, "Values cannot be negative.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        data.addIngredient(new Ingredient(n, u, q, m, up));
    }

    private void showReceiveDialog() {
        int vRow = table.getSelectedRow();
        if (vRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an ingredient first.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Ingredient sel = tableModel.getIngredientAt(table.convertRowIndexToModel(vRow));
        JTextField amount = new JTextField(12);
        amount.setToolTipText("Amount to add (" + sel.getUnit() + ")");
        int opt = JOptionPane.showConfirmDialog(
                this,
                new Object[] {"Adds to on-hand quantity (" + sel.getUnit() + ").", amount},
                "Receive stock — " + sel.getName(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) {
            return;
        }
        double a;
        try {
            a = Double.parseDouble(amount.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a numeric amount.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (a <= 0) {
            JOptionPane.showMessageDialog(this, "Amount must be positive.", "Ingredients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int mRow = table.convertRowIndexToModel(vRow);
        sel.setQuantityOnHand(sel.getQuantityOnHand() + a);
        tableModel.fireTableRowsUpdated(mRow, mRow);
        refreshLowStockLabel();
    }

    private static final class IngredientTableModel extends AbstractTableModel {
        private final BakeryDataService data;
        private final String[] cols = {
            "INGREDIENT", "CURRENT STOCK", "MIN STOCK", "UNIT PRICE", "TOTAL VALUE", "STATUS"
        };

        IngredientTableModel(BakeryDataService data) {
            this.data = data;
        }

        Ingredient getIngredientAt(int modelRow) {
            return data.getIngredients().get(modelRow);
        }

        @Override
        public int getRowCount() {
            return data.getIngredients().size();
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
            Ingredient ing = getIngredientAt(rowIndex);
            return switch (columnIndex) {
                case 0 -> ing.getName();
                case 1 -> qtyFormatStatic(ing.getQuantityOnHand()) + " " + ing.getUnit();
                case 2 -> ing.getMinimumLevel();
                case 3 -> ing.getUnitPrice();
                case 4 -> ing.stockValue();
                case 5 -> ing.isLowStock() ? "Low stock" : "In Stock";
                default -> null;
            };
        }

        private static String qtyFormatStatic(double q) {
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(0);
            return nf.format(q);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 2, 3, 4 -> Double.class;
                default -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0 || columnIndex == 2 || columnIndex == 3;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Ingredient ing = getIngredientAt(rowIndex);
            if (columnIndex == 0) {
                String s = String.valueOf(aValue).trim();
                if (s.isEmpty()) {
                    return;
                }
                ing.setName(s);
                data.ingredientsChanged();
            } else if (columnIndex == 2) {
                double v;
                try {
                    v = Double.parseDouble(String.valueOf(aValue).trim().replace(",", ""));
                } catch (NumberFormatException ex) {
                    return;
                }
                if (v < 0) {
                    return;
                }
                ing.setMinimumLevel(v);
                data.emitChange();
            } else if (columnIndex == 3) {
                double v;
                try {
                    v = Double.parseDouble(String.valueOf(aValue).trim().replace(",", ""));
                } catch (NumberFormatException ex) {
                    return;
                }
                if (v < 0) {
                    return;
                }
                ing.setUnitPrice(v);
                data.emitChange();
            }
        }
    }

    private static final class IngredientRowRenderer extends DefaultTableCellRenderer {
        private final IngredientTableModel model;
        private final NumberFormat qtyFormat;
        private final NumberFormat money;

        IngredientRowRenderer(IngredientTableModel model, NumberFormat qtyFormat, NumberFormat money) {
            this.model = model;
            this.qtyFormat = qtyFormat;
            this.money = money;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int mRow = table.convertRowIndexToModel(row);
            int mCol = table.convertColumnIndexToModel(column);
            Ingredient ing = mRow >= 0 && mRow < model.getRowCount() ? model.getIngredientAt(mRow) : null;

            l.setHorizontalAlignment(JLabel.LEFT);
            if (mCol == 2) {
                l.setHorizontalAlignment(JLabel.RIGHT);
                if (value instanceof Number n) {
                    l.setText(qtyFormat.format(n.doubleValue()));
                }
            }
            if (mCol == 3 || mCol == 4) {
                l.setHorizontalAlignment(JLabel.RIGHT);
                if (value instanceof Number n) {
                    l.setText(money.format(n.doubleValue()));
                }
            }
            if (mCol == 5 && ing != null && !isSelected) {
                if (ing.isLowStock()) {
                    l.setBackground(new Color(0xff, 0xeb, 0xee));
                    l.setForeground(new Color(0xc6, 0x28, 0x28));
                } else {
                    l.setBackground(new Color(0xe8, 0xf5, 0xe9));
                    l.setForeground(new Color(0x2e, 0x7d, 0x32));
                }
                l.setOpaque(true);
                l.setHorizontalAlignment(JLabel.CENTER);
                return l;
            }

            if (!isSelected && ing != null && ing.isLowStock()) {
                l.setBackground(UiTheme.LOW_STOCK_ROW);
                l.setForeground(table.getForeground());
            } else if (!isSelected) {
                l.setBackground(table.getBackground());
                l.setForeground(table.getForeground());
            }
            l.setOpaque(true);
            return l;
        }
    }
}
