package com.homebakery.ui.order;

import com.homebakery.model.BakeryOrder;
import com.homebakery.model.OrderLine;
import com.homebakery.model.OrderStatus;
import com.homebakery.model.Recipe;
import com.homebakery.model.RecipeLine;
import com.homebakery.service.BakeryDataService;
import com.homebakery.ui.BakeryFormat;
import com.homebakery.ui.ShellTheme;
import com.homebakery.ui.auth.RoundedCorners;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public final class NewOrderDialog {

    private NewOrderDialog() {
    }

    public static void show(Component parent, BakeryDataService data) {
        List<Recipe> catalog = data.getRecipes().stream()
                .sorted(Comparator.comparing(Recipe::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        if (catalog.isEmpty()) {
            JOptionPane.showMessageDialog(
                    parent,
                    "No products are configured. Add recipes first.",
                    "New Order",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Window owner = parent instanceof Window w ? w : SwingUtilities.getWindowAncestor(parent);
        JDialog dlg = new JDialog(owner, "New Order", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setResizable(true);

        NumberFormat peso = BakeryFormat.peso();

        Color fieldBorder = new Color(0xdd, 0xdd, 0xdd);
        var fieldOutline = BorderFactory.createCompoundBorder(
                RoundedCorners.outline(fieldBorder, RoundedCorners.RADIUS_FIELD, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12));
        var compactFieldOutline = BorderFactory.createCompoundBorder(
                RoundedCorners.outline(fieldBorder, RoundedCorners.RADIUS_FIELD, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(0xf0, 0xf2, 0xf5));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(ShellTheme.CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xea, 0xec, 0xf0)),
                BorderFactory.createEmptyBorder(24, 28, 20, 28)));

        JLabel title = new JLabel("New Order");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        title.setForeground(ShellTheme.TEXT_TITLE);
        JLabel subtitle = new JLabel("Select a product to see ingredients and pricing.");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitle.setForeground(ShellTheme.TEXT_SUB);
        JPanel head = new JPanel(new BorderLayout(0, 6));
        head.setOpaque(false);
        head.add(title, BorderLayout.NORTH);
        head.add(subtitle, BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JTextField customer = new JTextField();
        customer.setBorder(fieldOutline);
        customer.setFont(ShellTheme.BODY);

        String[] names = catalog.stream().map(Recipe::getName).toArray(String[]::new);
        JTextField productField = new JTextField(names.length == 0 ? "" : names[0]);
        productField.setEditable(false);
        productField.setBorder(fieldOutline);
        productField.setFont(ShellTheme.BODY);
        productField.setForeground(ShellTheme.TEXT_TITLE);
        productField.setBackground(Color.WHITE);

        JButton productPick = new JButton("▼");
        productPick.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        productPick.setFocusPainted(false);
        productPick.setBorder(fieldOutline);
        productPick.setBackground(Color.WHITE);
        productPick.setForeground(ShellTheme.TEXT_TITLE);
        productPick.setPreferredSize(new Dimension(44, 36));

        JPopupMenu productMenu = new JPopupMenu();
        for (String n : names) {
            JMenuItem item = new JMenuItem(n);
            item.setFont(ShellTheme.BODY);
            item.addActionListener(e -> productField.setText(n));
            productMenu.add(item);
        }
        productPick.addActionListener(e -> productMenu.show(productPick, 0, productPick.getHeight()));

        JPanel productPicker = new JPanel(new BorderLayout(8, 0));
        productPicker.setOpaque(false);
        productPicker.add(productField, BorderLayout.CENTER);
        productPicker.add(productPick, BorderLayout.EAST);

        JPanel ingredientsInner = new JPanel();
        ingredientsInner.setLayout(new BoxLayout(ingredientsInner, BoxLayout.Y_AXIS));
        ingredientsInner.setOpaque(false);
        JScrollPane ingScroll = new JScrollPane(ingredientsInner);
        ingScroll.setBorder(BorderFactory.createCompoundBorder(
                RoundedCorners.outline(fieldBorder, RoundedCorners.RADIUS_FIELD, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        ingScroll.setPreferredSize(new Dimension(440, 140));
        ingScroll.getViewport().setBackground(ShellTheme.CARD_WHITE);

        JTextField qty = new JTextField("1");
        qty.setPreferredSize(new Dimension(100, 40));
        qty.setBorder(compactFieldOutline);
        qty.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        qty.setHorizontalAlignment(JTextField.CENTER);
        qty.setForeground(ShellTheme.TEXT_TITLE);
        qty.setCaretColor(ShellTheme.TEXT_TITLE);
        qty.setSelectionColor(new Color(0xff, 0xcc, 0x80));
        qty.setSelectedTextColor(ShellTheme.TEXT_TITLE);

        LocalDate defaultDue = LocalDate.now().plusDays(1);
        Date initial = Date.from(defaultDue.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner dueSpinner = new JSpinner(new SpinnerDateModel(initial, null, null, Calendar.DAY_OF_MONTH));
        dueSpinner.setEditor(new JSpinner.DateEditor(dueSpinner, "MMM d, yyyy"));
        dueSpinner.setBorder(fieldOutline);
        dueSpinner.setFont(ShellTheme.BODY);

        JTextField unitPrice = new JTextField();
        unitPrice.setEditable(false);
        unitPrice.setBorder(fieldOutline);
        unitPrice.setFont(ShellTheme.BODY);
        unitPrice.setBackground(new Color(0xf9, 0xfa, 0xfb));
        unitPrice.setForeground(ShellTheme.TEXT_TITLE);

        JLabel totalLabel = new JLabel("Total: —");
        totalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        totalLabel.setForeground(ShellTheme.PRIMARY_ORANGE);

        Runnable refreshIngredients = () -> {
            refreshIngredientsForProduct(productField, data, ingredientsInner);
        };

        Runnable refreshPricing = () -> {
            String sel = productField.getText();
            data.findRecipeByName(sel == null ? "" : sel)
                    .ifPresentOrElse(
                            recipe -> {
                                unitPrice.setText(peso.format(recipe.getCostPerBatch()));
                                updateTotal(qty, recipe.getCostPerBatch(), totalLabel, peso);
                            },
                            () -> {
                                unitPrice.setText("—");
                                totalLabel.setText("Total: —");
                            });
        };

        Runnable refreshAll = () -> {
            refreshIngredients.run();
            refreshPricing.run();
        };

        productField.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                refreshAll.run();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                refreshAll.run();
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                refreshAll.run();
                            }
                        });

        qty.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                refreshTotalOnly(qty, productField, data, totalLabel, peso);
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                refreshTotalOnly(qty, productField, data, totalLabel, peso);
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                refreshTotalOnly(qty, productField, data, totalLabel, peso);
                            }
                        });

        int row = 0;
        gc.gridx = 0;
        gc.gridy = row;
        gc.gridwidth = 2;
        gc.insets = new Insets(0, 0, 4, 0);
        form.add(fieldLabel("Customer name *"), gc);
        gc.gridy = ++row;
        gc.insets = new Insets(0, 0, 0, 0);
        form.add(customer, gc);

        gc.gridy = ++row;
        gc.insets = new Insets(12, 0, 4, 0);
        form.add(fieldLabel("Product *"), gc);
        gc.gridy = ++row;
        gc.insets = new Insets(0, 0, 0, 0);
        form.add(productPicker, gc);

        gc.gridy = ++row;
        gc.insets = new Insets(10, 0, 4, 0);
        form.add(fieldLabel("Recipe ingredients"), gc);
        gc.gridy = ++row;
        form.add(ingScroll, gc);

        gc.gridy = ++row;
        gc.gridwidth = 1;
        gc.weightx = 0.4;
        gc.insets = new Insets(12, 0, 4, 0);
        form.add(fieldLabel("Quantity *"), gc);
        gc.gridx = 1;
        gc.insets = new Insets(12, 16, 4, 0);
        gc.weightx = 0.6;
        form.add(fieldLabel("Due date *"), gc);

        gc.gridy = ++row;
        gc.gridx = 0;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.weightx = 0.4;
        form.add(qty, gc);
        gc.gridx = 1;
        gc.insets = new Insets(0, 16, 0, 0);
        gc.weightx = 0.6;
        form.add(dueSpinner, gc);

        gc.gridy = ++row;
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = new Insets(12, 0, 4, 0);
        form.add(fieldLabel("Unit price (per item)"), gc);
        gc.gridy = ++row;
        gc.insets = new Insets(0, 0, 0, 0);
        form.add(unitPrice, gc);

        gc.gridy = ++row;
        gc.insets = new Insets(12, 0, 4, 0);
        form.add(fieldLabel("Total cost"), gc);
        gc.gridy = ++row;
        form.add(totalLabel, gc);

        refreshAll.run();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JButton cancel = new JButton("Cancel");
        cancel.setFont(ShellTheme.BODY);
        cancel.setForeground(ShellTheme.TEXT_SUB);
        cancel.setContentAreaFilled(false);
        cancel.setBorderPainted(false);
        cancel.setFocusPainted(false);
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.addActionListener(e -> dlg.dispose());

        RoundedOrangeButton submit = new RoundedOrangeButton("Create Order");
        submit.setPreferredSize(new Dimension(160, 44));
        submit.addActionListener(
                e -> {
                    String cName = customer.getText() == null ? "" : customer.getText().trim();
                    if (cName.isEmpty()) {
                        JOptionPane.showMessageDialog(dlg, "Customer name is required.", "New Order", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String pName = productField.getText() == null ? "" : productField.getText().trim();
                    if (pName == null || pName.isBlank()) {
                        JOptionPane.showMessageDialog(dlg, "Select a product.", "New Order", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Recipe recipe = data.findRecipeByName(pName).orElse(null);
                    if (recipe == null) {
                        JOptionPane.showMessageDialog(dlg, "Unknown product.", "New Order", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Date d = (Date) dueSpinner.getValue();
                    LocalDate dueDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int q;
                    try {
                        String qtyRaw = qty.getText() == null ? "" : qty.getText().trim();
                        if (!qtyRaw.matches("\\d+")) {
                            JOptionPane.showMessageDialog(dlg, "Use only numbers.", "New Order", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        q = Integer.parseInt(qtyRaw);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(
                                dlg, "Use only numbers.", "New Order", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (q <= 0) {
                        JOptionPane.showMessageDialog(dlg, "Quantity must be positive.", "New Order", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    double unit = recipe.getCostPerBatch();
                    BakeryOrder order = new BakeryOrder(cName, dueDate, OrderStatus.NEW);
                    order.getLines().add(new OrderLine(pName, q, unit));
                    data.addOrder(order);
                    dlg.dispose();
                });

        actions.add(cancel);
        actions.add(submit);

        card.add(head, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        dlg.setContentPane(root);
        dlg.pack();
        dlg.setMinimumSize(dlg.getPreferredSize());
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    private static void refreshTotalOnly(
            JTextField qty,
            JTextField productField,
            BakeryDataService data,
            JLabel totalLabel,
            NumberFormat peso) {
        String sel = productField.getText();
        data.findRecipeByName(sel == null ? "" : sel)
                .ifPresent(recipe -> updateTotal(qty, recipe.getCostPerBatch(), totalLabel, peso));
    }

    private static void refreshIngredientsForProduct(
            JTextField productField, BakeryDataService data, JPanel ingredientsInner) {
        ingredientsInner.removeAll();
        String sel = productField.getText();
        data.findRecipeByName(sel == null ? "" : sel)
                .ifPresentOrElse(
                        recipe -> {
                            JLabel sec = new JLabel("Ingredients:");
                            sec.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                            sec.setForeground(ShellTheme.TEXT_SUB);
                            sec.setAlignmentX(Component.LEFT_ALIGNMENT);
                            ingredientsInner.add(sec);
                            ingredientsInner.add(Box.createVerticalStrut(6));
                            for (RecipeLine line : recipe.getLines()) {
                                JPanel row = new JPanel(new BorderLayout(12, 0));
                                row.setOpaque(false);
                                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
                                JLabel left = new JLabel(line.getIngredientName());
                                left.setFont(ShellTheme.BODY);
                                JLabel right = new JLabel(line.getAmount());
                                right.setFont(ShellTheme.BODY);
                                right.setForeground(ShellTheme.TEXT_SUB);
                                row.add(left, BorderLayout.WEST);
                                row.add(right, BorderLayout.EAST);
                                ingredientsInner.add(row);
                            }
                        },
                        () -> {
                            JLabel empty = new JLabel("No ingredient list.");
                            empty.setForeground(ShellTheme.TEXT_SUB);
                            ingredientsInner.add(empty);
                        });
        ingredientsInner.revalidate();
        ingredientsInner.repaint();
    }

    private static void updateTotal(JTextField qtyField, double unitPrice, JLabel totalLabel, NumberFormat peso) {
        try {
            int q = Integer.parseInt(qtyField.getText().trim());
            if (q <= 0) {
                totalLabel.setText("Total: —");
                return;
            }
            totalLabel.setText("Total: " + peso.format(q * unitPrice));
        } catch (NumberFormatException ex) {
            totalLabel.setText("Total: —");
        }
    }

    private static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        l.setForeground(ShellTheme.TEXT_SUB);
        return l;
    }

    private static final class RoundedOrangeButton extends JButton {
        RoundedOrangeButton(String text) {
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
            int r = RoundedCorners.RADIUS_BUTTON;
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
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
