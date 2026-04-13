package com.homebakery.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Recipe {
    private final String id;
    private String name;
    private final List<RecipeLine> lines;
    private double costPerBatch;

    public Recipe(String name, double costPerBatch) {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name);
        this.lines = new ArrayList<>();
        this.costPerBatch = costPerBatch;
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

    public List<RecipeLine> getLines() {
        return lines;
    }

    public double getCostPerBatch() {
        return costPerBatch;
    }

    public void setCostPerBatch(double costPerBatch) {
        this.costPerBatch = costPerBatch;
    }
}
