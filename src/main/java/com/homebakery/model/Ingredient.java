package com.homebakery.model;

import java.util.Objects;
import java.util.UUID;

public final class Ingredient {
    private final String id;
    private String name;
    private String unit;
    private double quantityOnHand;
    private double minimumLevel;
    private double unitPrice;

    public Ingredient(String name, String unit, double quantityOnHand, double minimumLevel) {
        this(name, unit, quantityOnHand, minimumLevel, 0);
    }

    public Ingredient(String name, String unit, double quantityOnHand, double minimumLevel, double unitPrice) {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name);
        this.unit = Objects.requireNonNull(unit);
        this.quantityOnHand = quantityOnHand;
        this.minimumLevel = minimumLevel;
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = Objects.requireNonNull(unit);
    }

    public double getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(double quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public double getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(double minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public boolean isLowStock() {
        return quantityOnHand <= minimumLevel;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double stockValue() {
        return quantityOnHand * unitPrice;
    }
}
