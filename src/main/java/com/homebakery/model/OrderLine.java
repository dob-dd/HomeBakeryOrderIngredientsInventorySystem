package com.homebakery.model;

import java.util.Objects;

public final class OrderLine {
    private String productName;
    private int quantity;
    private double unitPrice;

    public OrderLine(String productName, int quantity, double unitPrice) {
        this.productName = Objects.requireNonNull(productName);
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = Objects.requireNonNull(productName);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double lineTotal() {
        return quantity * unitPrice;
    }
}
