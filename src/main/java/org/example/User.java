package org.example;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String login;
    private String password;
    private List<Category> incomes;
    private List<Category> outcomes;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public List<Category> getIncomes() {
        return incomes;
    }

    public List<Category> getOutcomes() {
        return outcomes;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.incomes = new ArrayList<>();
        this.outcomes = new ArrayList<>();
        incomes.add(new Category("прочее"));
        outcomes.add(new Category("прочее"));
    }

    private List<Category> getCategories(char type) {
        if (type == '+') {
            return incomes;
        } else if (type == '-') {
            return outcomes;
        } else {
            throw new IllegalArgumentException("Неверный символ операции");
        }
    }

    private Category findCategoryByName(List<Category> categories, String categoryName) {
        Category category = null;

        for (Category c : categories) {
            if (c.getName().equals(categoryName)) {
                category = c;
                break;
            }
        }

        return category;
    }

    private Category addCategory(char type, String name, double limit) {
        List<Category> categories = getCategories(type);

        Category category = findCategoryByName(categories, name);

        if (category == null) {
            category = new Category(name, limit);
            categories.add(category);
        } else {
            category.setLimit(limit);
        }

        return category;
    }

    public Category addCategory(String name, double limit) {
        return addCategory('-', name, limit);
    }

    public Category addCategory(String name) {
        return addCategory('+', name, 0);
    }

    public void addAmount(char type, double amount, String categoryName) {
        List<Category> categories = getCategories(type);

        Category category = findCategoryByName(categories, categoryName);

        if (category == null) category = addCategory(type, categoryName, 0);

        category.add(amount);
    }

    public double sum(char type) {
        List<Category> categories = getCategories(type);
        double sum = 0;

        for (Category c : categories) sum += c.getSum();

        return sum;
    }

    public void renameCategory(char type, String categoryName, String newName) {
        List<Category> categories = getCategories(type);

        Category category = findCategoryByName(categories, categoryName);

        if (category != null) category.setName(newName);
    }
}