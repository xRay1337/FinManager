package org.example;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private double limit;
    private List<Double> amounts;

    public Category(String name, double limit) {
        this.name = name;
        this.limit = limit;
        this.amounts = new ArrayList<>();
    }

    public Category(String name) {
        this(name, 0);
    }

    public Category(String name, double limit, List<Double> amounts) {
        this(name, limit);

        this.amounts = amounts == null ? new ArrayList<>() : amounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public List<Double> getAmounts() {
        return amounts;
    }

    public boolean add(double amount) {
        amounts.add(amount);
        return getSum() <= limit;
    }

    public double getSum() {
        return amounts.stream().mapToDouble(Double::doubleValue).sum();
    }
}