package com.homebakery.ui;

import com.homebakery.model.Recipe;
import com.homebakery.model.RecipeLine;
import com.homebakery.service.BakeryDataService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.NumberFormat;

public final class RecipesPane extends JPanel {
    private final BakeryDataService data;
    private final NumberFormat money = BakeryFormat.peso();
    private final JPanel cardsRow = new JPanel();

    public RecipesPane(BakeryDataService data) {
        super(new BorderLayout());
        this.data = data;
        setOpaque(false);

        JLabel head = new JLabel("Recipes");
        head.setFont(ShellTheme.TITLE_LG);
        head.setForeground(ShellTheme.TEXT_TITLE);
        JLabel sub = new JLabel("Manage recipes and calculate costs");
        sub.setFont(ShellTheme.BODY);
        sub.setForeground(ShellTheme.TEXT_SUB);

        JPanel titles = new JPanel(new BorderLayout(0, 4));
        titles.setOpaque(false);
        titles.add(head, BorderLayout.NORTH);
        titles.add(sub, BorderLayout.SOUTH);

        JButton newRecipe = new JButton("+ New Recipe");
        newRecipe.setOpaque(true);
        newRecipe.setBackground(ShellTheme.PRIMARY_ORANGE);
        newRecipe.setForeground(Color.WHITE);
        newRecipe.setFocusPainted(false);
        newRecipe.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        newRecipe.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        newRecipe.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Add recipe from Inventory and Orders data in a future update.\n"
                        + "For now, use the seeded Chocolate Cake, Croissants, and Sourdough cards.",
                "Recipes",
                JOptionPane.INFORMATION_MESSAGE));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(titles, BorderLayout.WEST);
        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        east.setOpaque(false);
        east.add(newRecipe);
        header.add(east, BorderLayout.EAST);

        cardsRow.setLayout(new BoxLayout(cardsRow, BoxLayout.X_AXIS));
        cardsRow.setOpaque(false);
        cardsRow.setAlignmentX(LEFT_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(cardsRow);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ShellTheme.WORKSPACE_BG);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(header, BorderLayout.NORTH);

        add(north, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        data.addChangeListener(() -> SwingUtilities.invokeLater(this::rebuildCards));
        rebuildCards();
    }

    private void rebuildCards() {
        cardsRow.removeAll();
        for (Recipe r : data.getRecipes()) {
            cardsRow.add(Box.createHorizontalStrut(16));
            cardsRow.add(recipeCard(r));
        }
        cardsRow.add(Box.createHorizontalGlue());
        cardsRow.revalidate();
        cardsRow.repaint();
    }

    private JPanel recipeCard(Recipe r) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(ShellTheme.CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xea, 0xec, 0xf0)),
                new EmptyBorder(16, 18, 16, 18)));
        card.setPreferredSize(new Dimension(280, 320));
        card.setMaximumSize(new Dimension(320, 400));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel name = new JLabel(r.getName());
        name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        name.setForeground(ShellTheme.TEXT_TITLE);
        JPanel icons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        icons.setOpaque(false);
        JButton edit = new JButton("✎");
        edit.setToolTipText("Edit");
        edit.setFocusPainted(false);
        edit.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        edit.setForeground(new Color(0x25, 0x63, 0xeb));
        edit.setContentAreaFilled(false);
        edit.addActionListener(e -> JOptionPane.showMessageDialog(this, "Recipe editor coming soon.", "Recipes", JOptionPane.INFORMATION_MESSAGE));
        JButton del = new JButton("\uD83D\uDDD1");
        del.setToolTipText("Delete");
        del.setFocusPainted(false);
        del.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        del.setForeground(ShellTheme.LOGOUT_RED);
        del.setContentAreaFilled(false);
        del.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(
                    this, "Delete recipe \"" + r.getName() + "\"?", "Recipes", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                data.removeRecipe(r);
            }
        });
        icons.add(edit);
        icons.add(del);
        titleRow.add(name, BorderLayout.WEST);
        titleRow.add(icons, BorderLayout.EAST);

        JLabel ingTitle = new JLabel("Ingredients:");
        ingTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        ingTitle.setForeground(ShellTheme.TEXT_SUB);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        for (RecipeLine line : r.getLines()) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            JLabel left = new JLabel(line.getIngredientName());
            left.setFont(ShellTheme.BODY);
            JLabel right = new JLabel(line.getAmount());
            right.setFont(ShellTheme.BODY);
            right.setForeground(ShellTheme.TEXT_SUB);
            row.add(left, BorderLayout.WEST);
            row.add(right, BorderLayout.EAST);
            list.add(row);
        }

        JPanel mid = new JPanel(new BorderLayout(0, 8));
        mid.setOpaque(false);
        mid.add(ingTitle, BorderLayout.NORTH);
        mid.add(list, BorderLayout.CENTER);

        javax.swing.JSeparator sepBar = new javax.swing.JSeparator();
        sepBar.setForeground(new Color(0xea, 0xec, 0xf0));

        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        JLabel costLbl = new JLabel("Cost per batch");
        costLbl.setFont(ShellTheme.BODY);
        costLbl.setForeground(ShellTheme.TEXT_SUB);
        double computedBatchCost = data.calculateRecipeCost(r);
        JLabel costVal = new JLabel(money.format(computedBatchCost));
        costVal.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        costVal.setForeground(ShellTheme.PRIMARY_ORANGE);
        foot.add(costLbl, BorderLayout.WEST);
        foot.add(costVal, BorderLayout.EAST);

        card.add(titleRow, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.setOpaque(false);
        south.add(sepBar, BorderLayout.NORTH);
        south.add(foot, BorderLayout.SOUTH);
        card.add(south, BorderLayout.SOUTH);

        return card;
    }
}
