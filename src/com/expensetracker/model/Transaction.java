package com.expensetracker.model;

import java.time.LocalDate;



public class Transaction {

    private int id;
    private double amount;
    private LocalDate date;
    private String category;
    private TransactionType type;
    private String notes;

    // Constructor for INSERT (no ID yet)
    public Transaction(double amount, LocalDate date, String category,
                       TransactionType type, String notes) {
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.type = type;
        this.notes = notes;
    }

    // Constructor for SELECT (with ID)
    public Transaction(int id, double amount, LocalDate date, String category,
                       TransactionType type, String notes) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.type = type;
        this.notes = notes;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
