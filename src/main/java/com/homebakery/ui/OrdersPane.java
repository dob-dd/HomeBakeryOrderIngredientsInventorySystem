package com.homebakery.ui;

import com.homebakery.model.BakeryOrder;
import com.homebakery.model.OrderLine;
import com.homebakery.model.OrderStatus;
import com.homebakery.service.BakeryDataService;
import com.homebakery.ui.order.NewOrderDialog;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class OrdersPane extends JPanel {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final BakeryDataService data;
    private final NumberFormat money = BakeryFormat.peso();
    private final OrdersTableModel ordersModel;
    private final LinesTableModel linesModel = new LinesTableModel();
    private final JTable ordersTable = new JTable();
    private final JTable linesTable = new JTable(linesModel);
    private final JComboBox<OrderStatus> statusBox = new JComboBox<>(OrderStatus.values());
    private final JLabel pickLabel = new JLabel("Select an order");
    private final JLabel totalLabel = new JLabel(" ");
    private final JButton deleteBtn = new JButton("Delete order");
    private boolean suppressStatusEvents;

    public OrdersPane(BakeryDataService data) {
        super(new BorderLayout());
        this.data = data;
        setOpaque(false);

        this.ordersModel = new OrdersTableModel(data);
        ordersTable.setModel(ordersModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setRowHeight(24);
        ordersTable.setFillsViewportHeight(true);
        ordersTable.setShowGrid(false);
        ordersTable.getTableHeader().setBackground(UiTheme.TABLE_HEADER);
        ordersTable.getTableHeader().setFont(ordersTable.getFont().deriveFont(java.awt.Font.BOLD));

        ordersTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof LocalDate ld) {
                    l.setText(DATE_FMT.format(ld));
                }
                return l;
            }
        });

        ordersTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Number n) {
                    l.setText(money.format(n.doubleValue()));
                }
                return l;
            }
        });

        ordersTable.getColumnModel().getColumn(2).setCellRenderer(OrderStatusUi.badgeRenderer());

        TableColumn c0 = ordersTable.getColumnModel().getColumn(0);
        c0.setPreferredWidth(160);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        linesTable.setRowHeight(22);
        linesTable.setFillsViewportHeight(true);
        linesTable.setShowGrid(false);
        linesTable.getTableHeader().setBackground(UiTheme.TABLE_HEADER);
        linesTable.getTableHeader().setFont(linesTable.getFont().deriveFont(java.awt.Font.BOLD));
        linesModel.initColumnWidths(linesTable);

        statusBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OrderStatus s) {
                    l.setText(OrderStatusUi.label(s));
                }
                return l;
            }
        });

        statusBox.addItemListener(e -> {
            if (suppressStatusEvents || e.getItem() == null) {
                return;
            }
            int row = ordersTable.getSelectedRow();
            if (row < 0) {
                return;
            }
            BakeryOrder sel = data.getOrders().get(ordersTable.convertRowIndexToModel(row));
            data.updateOrderStatus(sel, (OrderStatus) statusBox.getSelectedItem());
        });

        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = ordersTable.getSelectedRow();
            if (row < 0) {
                clearDetail();
                return;
            }
            BakeryOrder sel = data.getOrders().get(ordersTable.convertRowIndexToModel(row));
            pickLabel.setText(sel.getCustomerName() + " · due " + DATE_FMT.format(sel.getDueDate()));
            suppressStatusEvents = true;
            statusBox.setSelectedItem(sel.getStatus());
            suppressStatusEvents = false;
            statusBox.setEnabled(true);
            deleteBtn.setEnabled(true);
            linesModel.setOrder(sel);
            totalLabel.setText("Subtotal: " + money.format(sel.subtotal()));
        });

        deleteBtn.setEnabled(false);
        statusBox.setEnabled(false);
        deleteBtn.setBackground(UiTheme.DANGER_BG);
        deleteBtn.setForeground(UiTheme.DANGER_FG);
        deleteBtn.addActionListener(e -> {
            int row = ordersTable.getSelectedRow();
            if (row < 0) {
                return;
            }
            BakeryOrder sel = data.getOrders().get(ordersTable.convertRowIndexToModel(row));
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "Remove order for " + sel.getCustomerName() + "?",
                    "Delete order",
                    JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                data.removeOrder(sel);
            }
        });

        JButton newOrder = new PrimaryOrangeButton("+ New Order");
        newOrder.setPreferredSize(new Dimension(160, 40));
        newOrder.addActionListener(e -> NewOrderDialog.show(this, data));

        JLabel title = new JLabel("Orders");
        title.setFont(ShellTheme.TITLE_LG);
        title.setForeground(ShellTheme.TEXT_TITLE);
        JLabel subtitle = new JLabel("Manage customer orders and fulfillment.");
        subtitle.setForeground(ShellTheme.TEXT_SUB);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel leftHead = new JPanel(new BorderLayout());
        leftHead.setOpaque(false);
        leftHead.add(title, BorderLayout.NORTH);
        leftHead.add(subtitle, BorderLayout.SOUTH);
        header.add(leftHead, BorderLayout.WEST);
        header.add(newOrder, BorderLayout.EAST);

        JPanel detail = new JPanel();
        detail.setLayout(new GridBagLayout());
        detail.setBackground(UiTheme.CARD);
        detail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xe8, 0xdf, 0xd5)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, 0, 8, 0);
        JLabel detailTitle = new JLabel("Order detail");
        detailTitle.setFont(detailTitle.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        detail.add(detailTitle, gc);
        gc.gridy++;
        pickLabel.setForeground(UiTheme.TEXT_MUTED);
        detail.add(pickLabel, gc);
        gc.gridy++;
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(new JLabel("Status:"));
        actions.add(statusBox);
        actions.add(deleteBtn);
        detail.add(actions, gc);
        gc.gridy++;
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(0, 0, 8, 0);
        JScrollPane linesScroll = new JScrollPane(linesTable);
        linesScroll.setPreferredSize(new Dimension(200, 160));
        detail.add(linesScroll, gc);
        gc.gridy++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weighty = 0;
        totalLabel.setFont(totalLabel.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        detail.add(totalLabel, gc);

        JScrollPane ordersScroll = new JScrollPane(ordersTable);
        ordersScroll.setMinimumSize(new Dimension(200, 120));
        detail.setMinimumSize(new Dimension(200, 180));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ordersScroll, detail);
        split.setResizeWeight(0.55);
        split.setBorder(null);
        split.setOpaque(false);

        JPanel inner = new JPanel(new BorderLayout(0, 14));
        inner.setOpaque(false);
        inner.add(header, BorderLayout.NORTH);
        inner.add(split, BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ShellTheme.CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xea, 0xec, 0xf0)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        card.add(inner, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        data.addChangeListener(() -> SwingUtilities.invokeLater(() -> {
            ordersModel.fireTableDataChanged();
            int vRow = ordersTable.getSelectedRow();
            if (vRow >= 0) {
                int mRow = ordersTable.convertRowIndexToModel(vRow);
                if (mRow >= 0 && mRow < data.getOrders().size()) {
                    BakeryOrder sel = data.getOrders().get(mRow);
                    pickLabel.setText(sel.getCustomerName() + " · due " + DATE_FMT.format(sel.getDueDate()));
                    suppressStatusEvents = true;
                    statusBox.setSelectedItem(sel.getStatus());
                    suppressStatusEvents = false;
                    linesModel.setOrder(sel);
                    totalLabel.setText("Subtotal: " + money.format(sel.subtotal()));
                    return;
                }
            }
            clearDetail();
        }));
    }

    private void clearDetail() {
        pickLabel.setText("Select an order");
        suppressStatusEvents = true;
        statusBox.setSelectedItem(null);
        suppressStatusEvents = false;
        statusBox.setEnabled(false);
        deleteBtn.setEnabled(false);
        linesModel.setOrder(null);
        totalLabel.setText(" ");
    }

    private static final class OrdersTableModel extends AbstractTableModel {
        private final BakeryDataService data;
        private final String[] cols = {"Customer", "Due", "Status", "Total"};

        OrdersTableModel(BakeryDataService data) {
            this.data = data;
        }

        @Override
        public int getRowCount() {
            return data.getOrders().size();
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
            BakeryOrder o = data.getOrders().get(rowIndex);
            return switch (columnIndex) {
                case 0 -> o.getCustomerName();
                case 1 -> o.getDueDate();
                case 2 -> o.getStatus();
                case 3 -> o.subtotal();
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 1 -> LocalDate.class;
                case 2 -> OrderStatus.class;
                case 3 -> Double.class;
                default -> String.class;
            };
        }
    }

    private final class LinesTableModel extends AbstractTableModel {
        private BakeryOrder order;
        private final String[] cols = {"Item", "Qty", "Unit", "Line total"};

        void setOrder(BakeryOrder order) {
            this.order = order;
            fireTableDataChanged();
        }

        void initColumnWidths(JTable t) {
            t.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (value instanceof Number n) {
                        l.setText(money.format(n.doubleValue()));
                        l.setHorizontalAlignment(JLabel.RIGHT);
                    }
                    return l;
                }
            });
            t.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (value instanceof Number n) {
                        l.setText(money.format(n.doubleValue()));
                        l.setHorizontalAlignment(JLabel.RIGHT);
                    }
                    return l;
                }
            });
        }

        @Override
        public int getRowCount() {
            return order == null ? 0 : order.getLines().size();
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
            OrderLine line = order.getLines().get(rowIndex);
            return switch (columnIndex) {
                case 0 -> line.getProductName();
                case 1 -> line.getQuantity();
                case 2 -> line.getUnitPrice();
                case 3 -> line.lineTotal();
                default -> null;
            };
        }
    }

    private static final class PrimaryOrangeButton extends JButton {
        PrimaryOrangeButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = ShellTheme.PRIMARY_ORANGE;
            if (getModel().isPressed()) {
                fill = fill.darker();
            } else if (getModel().isRollover()) {
                fill = new Color(
                        Math.min(255, fill.getRed() + 12),
                        Math.min(255, fill.getGreen() + 10),
                        Math.min(255, fill.getBlue() + 5));
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String t = getText();
            int tx = (getWidth() - fm.stringWidth(t)) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(t, tx, ty);
            g2.dispose();
        }
    }
}
