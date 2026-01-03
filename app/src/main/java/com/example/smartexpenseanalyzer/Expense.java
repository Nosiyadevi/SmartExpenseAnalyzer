package com.example.smartexpenseanalyzer;

public class Expense {

    int id;
    double amount;
    String category;
    String date;
    String userId;

    public Expense(int id, double amount, String category, String date, String userId) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.userId = userId;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getUserId() { return userId; }
}
