package com.homebakery.model;

import java.util.Objects;

public final class RecipeLine {
    private String ingredientName;
    private String amount;

    public RecipeLine(String ingredientName, String amount) {
        this.ingredientName = Objects.requireNonNull(ingredientName);
        this.amount = Objects.requireNonNull(amount);
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = Objects.requireNonNull(ingredientName);
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = Objects.requireNonNull(amount);
    }
}
