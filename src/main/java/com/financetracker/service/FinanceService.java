package com.financetracker.service;

import com.financetracker.dao.TransactionDAO;
import com.financetracker.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FinanceService {

    private TransactionDAO transactionDAO = new TransactionDAO();

    public boolean addTransaction(int userId, String type, String category,
                                   String amountStr, String description, String dateStr) {
        // Validate type
        if (!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSE")) {
            System.out.println("Type must be INCOME or EXPENSE.");
            return false;
        }

        // Validate amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Amount must be greater than zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
            return false;
        }

        // Validate date
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr); // expects yyyy-MM-dd
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date. Use format: yyyy-MM-dd");
            return false;
        }

        // Validate category
        if (category == null || category.trim().isEmpty()) {
            System.out.println("Category cannot be empty.");
            return false;
        }

        // All good — create and save
        Transaction t = new Transaction(
            userId,
            type.toUpperCase(),
            category.trim(),
            amount,
            description.trim(),
            date
        );

        boolean success = transactionDAO.addTransaction(t);
        if (success) {
            System.out.println("Transaction saved successfully!");
        }
        return success;
    }

    public void viewTransactions(int userId) {
        List<Transaction> list = transactionDAO.getTransactionsByUser(userId);

        if (list.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\n======= Your Transactions =======");
        for (Transaction t : list) {
            System.out.println(t); // calls toString() from Transaction.java
        }
        System.out.println("=================================");
    }

    public void viewMonthlySummary(int userId, int month, int year) {
        transactionDAO.getMonthlySummary(userId, month, year);
    }
}