package com.homebakery.service;

import com.homebakery.model.BakeryOrder;
import com.homebakery.model.Ingredient;
import com.homebakery.model.OrderLine;
import com.homebakery.model.OrderStatus;
import com.homebakery.model.Recipe;
import com.homebakery.model.RecipeLine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BakeryDataService {
    private static final Pattern AMOUNT_PATTERN =
            Pattern.compile("^\\s*([0-9]+(?:\\.[0-9]+)?|[0-9]+/[0-9]+)\\s*([a-zA-Z]+)\\s*$");

    private final List<BakeryOrder> orders = new ArrayList<>();
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<Recipe> recipes = new ArrayList<>();
    private final List<Runnable> changeListeners = new CopyOnWriteArrayList<>();

    public BakeryDataService() {
        seed();
    }

    public List<BakeryOrder> getOrders() {
        return orders;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    /** Case-insensitive match for order / catalog dialogs. */
    public Optional<Recipe> findRecipeByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String n = name.trim();
        return recipes.stream().filter(r -> r.getName().equalsIgnoreCase(n)).findFirst();
    }

    public long pendingOrderCount() {
        return orders.stream().filter(o -> o.getStatus() == OrderStatus.NEW).count();
    }

    public long inProgressOrderCount() {
        return orders.stream().filter(o -> o.getStatus() == OrderStatus.IN_PROGRESS).count();
    }

    /** Sum of order subtotals excluding cancelled (dashboard revenue). */
    public double totalRevenue() {
        return orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(BakeryOrder::subtotal)
                .sum();
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
        notifyChanged();
    }

    public void removeRecipe(Recipe recipe) {
        if (recipes.remove(recipe)) {
            notifyChanged();
        }
    }

    /**
     * Calculates recipe batch cost from ingredient amount x ingredient unit price.
     * Missing ingredients or unparsable amounts are skipped.
     */
    public double calculateRecipeCost(Recipe recipe) {
        if (recipe == null) {
            return 0;
        }
        double total = 0;
        for (RecipeLine line : recipe.getLines()) {
            Optional<Ingredient> ingredientOpt = ingredients.stream()
                    .filter(i -> i.getName().equalsIgnoreCase(line.getIngredientName()))
                    .findFirst();
            if (ingredientOpt.isEmpty()) {
                continue;
            }
            ParsedAmount parsed = parseAmount(line.getAmount());
            if (parsed == null) {
                continue;
            }
            Ingredient ingredient = ingredientOpt.get();
            double amountInIngredientUnit = convertUnits(parsed.value(), parsed.unit(), ingredient.getUnit());
            total += amountInIngredientUnit * ingredient.getUnitPrice();
        }
        return total;
    }

    public void addChangeListener(Runnable listener) {
        changeListeners.add(Objects.requireNonNull(listener));
    }

    public void removeChangeListener(Runnable listener) {
        changeListeners.remove(listener);
    }

    private void notifyChanged() {
        for (Runnable r : changeListeners) {
            r.run();
        }
    }

    /** Notifies listeners without re-sorting (e.g. ingredient field edits that affect stock warnings). */
    public void emitChange() {
        notifyChanged();
    }

    public void addOrder(BakeryOrder order) {
        orders.add(order);
        consumeIngredientsForOrder(order);
        sortOrders();
        notifyChanged();
    }

    public void updateOrderStatus(BakeryOrder order, OrderStatus newStatus) {
        if (order == null || newStatus == null) {
            return;
        }
        OrderStatus oldStatus = order.getStatus();
        if (oldStatus == newStatus) {
            return;
        }
        if (newStatus == OrderStatus.CANCELLED) {
            restoreIngredientsForOrder(order);
        } else if (oldStatus == OrderStatus.CANCELLED) {
            consumeIngredientsForOrder(order);
        }
        order.setStatus(newStatus);
        sortOrders();
        notifyChanged();
    }

    public void removeOrder(BakeryOrder order) {
        if (orders.remove(order)) {
            notifyChanged();
        }
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        sortIngredients();
        notifyChanged();
    }

    public void removeIngredient(Ingredient ingredient) {
        if (ingredients.remove(ingredient)) {
            notifyChanged();
        }
    }

    public int lowStockCount() {
        return (int) ingredients.stream().filter(Ingredient::isLowStock).count();
    }

    public void ordersChanged() {
        sortOrders();
        notifyChanged();
    }

    public void ingredientsChanged() {
        sortIngredients();
        notifyChanged();
    }

    private void sortOrders() {
        orders.sort(Comparator.comparing(BakeryOrder::getDueDate).thenComparing(BakeryOrder::getCustomerName));
    }

    private void sortIngredients() {
        ingredients.sort(Comparator.comparing(Ingredient::getName, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Deducts ingredient on-hand quantities using the selected product recipe and ordered quantity.
     * Example: 2 Chocolate Cakes deduct 2x each listed recipe amount.
     */
    private void consumeIngredientsForOrder(BakeryOrder order) {
        applyIngredientDeltaForOrder(order, -1.0);
    }

    private void restoreIngredientsForOrder(BakeryOrder order) {
        applyIngredientDeltaForOrder(order, 1.0);
    }

    private void applyIngredientDeltaForOrder(BakeryOrder order, double direction) {
        for (OrderLine line : order.getLines()) {
            Optional<Recipe> recipeOpt = findRecipeByName(line.getProductName());
            if (recipeOpt.isEmpty()) {
                continue;
            }
            Recipe recipe = recipeOpt.get();
            for (RecipeLine recipeLine : recipe.getLines()) {
                Optional<Ingredient> ingredientOpt = ingredients.stream()
                        .filter(i -> i.getName().equalsIgnoreCase(recipeLine.getIngredientName()))
                        .findFirst();
                if (ingredientOpt.isEmpty()) {
                    continue;
                }
                ParsedAmount parsed = parseAmount(recipeLine.getAmount());
                if (parsed == null) {
                    continue;
                }
                Ingredient ingredient = ingredientOpt.get();
                double converted = convertUnits(parsed.value(), parsed.unit(), ingredient.getUnit());
                double delta = direction * converted * line.getQuantity();
                ingredient.setQuantityOnHand(ingredient.getQuantityOnHand() + delta);
            }
        }
    }

    private static ParsedAmount parseAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        Matcher m = AMOUNT_PATTERN.matcher(raw);
        if (!m.matches()) {
            return null;
        }
        double value = parseNumericPart(m.group(1));
        if (Double.isNaN(value)) {
            return null;
        }
        return new ParsedAmount(value, m.group(2));
    }

    private static double parseNumericPart(String valueRaw) {
        if (valueRaw == null || valueRaw.isBlank()) {
            return Double.NaN;
        }
        if (valueRaw.contains("/")) {
            String[] fraction = valueRaw.split("/", 2);
            if (fraction.length != 2) {
                return Double.NaN;
            }
            try {
                double numerator = Double.parseDouble(fraction[0]);
                double denominator = Double.parseDouble(fraction[1]);
                if (denominator == 0) {
                    return Double.NaN;
                }
                return numerator / denominator;
            } catch (NumberFormatException ex) {
                return Double.NaN;
            }
        }
        try {
            return Double.parseDouble(valueRaw);
        } catch (NumberFormatException ex) {
            return Double.NaN;
        }
    }

    private static double convertUnits(double value, String fromUnitRaw, String toUnitRaw) {
        String from = normalizeUnit(fromUnitRaw);
        String to = normalizeUnit(toUnitRaw);
        if (from.equals(to)) {
            return value;
        }
        if ("g".equals(from) && "kg".equals(to)) {
            return value / 1000.0;
        }
        if ("kg".equals(from) && "g".equals(to)) {
            return value * 1000.0;
        }
        if ("ml".equals(from) && "l".equals(to)) {
            return value / 1000.0;
        }
        if ("l".equals(from) && "ml".equals(to)) {
            return value * 1000.0;
        }
        // Unknown conversion pair: consume as-is to avoid blocking deduction.
        return value;
    }

    private static String normalizeUnit(String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase();
        return switch (u) {
            case "pcs", "pc", "piece", "pieces" -> "each";
            case "gram", "grams" -> "g";
            case "kilogram", "kilograms" -> "kg";
            case "liter", "liters" -> "l";
            case "milliliter", "milliliters" -> "ml";
            default -> u;
        };
    }

    private record ParsedAmount(double value, String unit) {}

    private void seed() {
        BakeryOrder o1 = new BakeryOrder("A. Rivera", LocalDate.now().plusDays(2), OrderStatus.NEW);
        o1.getLines().add(new OrderLine("Sourdough loaf", 2, 7.50));
        o1.getLines().add(new OrderLine("Cinnamon rolls (6)", 1, 14.00));

        BakeryOrder o2 = new BakeryOrder("Jordan Lee", LocalDate.now(), OrderStatus.IN_PROGRESS);
        o2.getLines().add(new OrderLine("Custom birthday cake", 1, 68.00));

        BakeryOrder o3 = new BakeryOrder("Cafe North", LocalDate.now().minusDays(1), OrderStatus.READY);
        o3.getLines().add(new OrderLine("Morning pastries (dozen)", 3, 36.00));

        BakeryOrder o4 = new BakeryOrder("Charlz Nicholaz", LocalDate.now().plusDays(1), OrderStatus.NEW);
        o4.getLines().add(new OrderLine("Chocolate Cake", 2, 22.50));

        BakeryOrder o5 = new BakeryOrder("Dan Kenneth Nocete", LocalDate.now(), OrderStatus.IN_PROGRESS);
        o5.getLines().add(new OrderLine("Croissants", 12, 2.00));

        orders.clear();
        Collections.addAll(orders, o1, o2, o3, o4, o5);
        sortOrders();

        ingredients.clear();
        ingredients.addAll(
                List.of(
                        new Ingredient("Flour", "kg", 50, 10, 2.5),
                        new Ingredient("Sugar", "kg", 20, 5, 3.0),
                        new Ingredient("Butter", "kg", 8, 2, 8.5),
                        new Ingredient("Eggs", "each", 120, 48, 0.35),
                        new Ingredient("Milk", "L", 24, 8, 1.2),
                        new Ingredient("Chocolate", "kg", 5, 1, 12.0),
                        new Ingredient("Yeast", "g", 500, 200, 0.02),
                        new Ingredient("Salt", "kg", 3, 1, 0.8)));
        sortIngredients();

        recipes.clear();
        Recipe r1 = new Recipe("Chocolate Cake", 200);
        r1.getLines()
                .addAll(
                        List.of(
                                new RecipeLine("Flour", "2 kg"),
                                new RecipeLine("Sugar", "1.5 kg"),
                                new RecipeLine("Butter", "0.5 kg"),
                                new RecipeLine("Eggs", "6 pcs"),
                                new RecipeLine("Milk", "1 L"),
                                new RecipeLine("Chocolate", "0.5 kg")));
        Recipe r2 = new Recipe("Croissants", 75);
        r2.getLines()
                .addAll(
                        List.of(
                                new RecipeLine("Flour", "3 kg"),
                                new RecipeLine("Butter", "2 kg"),
                                new RecipeLine("Yeast", "50 g")));
        Recipe r3 = new Recipe("Sourdough Bread", 80);
        r3.getLines()
                .addAll(
                        List.of(
                                new RecipeLine("Flour", "1.5 kg"),
                                new RecipeLine("Salt", "25 g"),
                                new RecipeLine("Yeast", "10 g")));
        recipes.addAll(List.of(r1, r2, r3));
    }
}
