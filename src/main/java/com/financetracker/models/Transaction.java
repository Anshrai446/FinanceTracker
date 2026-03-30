package com.financetracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private int id;
    private int userId;
    private String type;        // "INCOME" or "EXPENSE"
    private String category;
    private BigDecimal amount;  // BigDecimal for money — never use float/double for currency
    private String description;
    private LocalDate date;

    // Constructor for creating new transaction (no id yet)
    public Transaction(int userId, String type, String category,
                       BigDecimal amount, String description, LocalDate date) {
        this.userId = userId;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Constructor for fetching from DB (has id)
    public Transaction(int id, int userId, String type, String category,
                       BigDecimal amount, String description, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s - Rs.%.2f - %s",
            type, category, description, amount, date);
    }
}