package com.homebakery.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class BakeryOrder {
    private final String id;
    private String customerName;
    private LocalDate dueDate;
    private OrderStatus status;
    private final List<OrderLine> lines;

    public BakeryOrder(String customerName, LocalDate dueDate, OrderStatus status) {
        this.id = UUID.randomUUID().toString();
        this.customerName = Objects.requireNonNull(customerName);
        this.dueDate = Objects.requireNonNull(dueDate);
        this.status = Objects.requireNonNull(status);
        this.lines = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = Objects.requireNonNull(customerName);
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public double subtotal() {
        return lines.stream().mapToDouble(OrderLine::lineTotal).sum();
    }
}
