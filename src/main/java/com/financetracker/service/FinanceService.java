package com.financetracker.service;

import com.financetracker.ai.AIAdvisor;
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
        if (!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSE")) {
            System.out.println("Type must be INCOME or EXPENSE.");
            return false;
        }
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
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date. Use format: yyyy-MM-dd");
            return false;
        }
        if (category == null || category.trim().isEmpty()) {
            System.out.println("Category cannot be empty.");
            return false;
        }
        Transaction t = new Transaction(
            userId, type.toUpperCase(), category.trim(),
            amount, description.trim(), date
        );
        boolean success = transactionDAO.addTransaction(t);
        if (success) System.out.println("Transaction saved successfully!");
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
            System.out.println(t);
        }
        System.out.println("=================================");
    }

    public void viewMonthlySummary(int userId, int month, int year) {
        transactionDAO.getMonthlySummary(userId, month, year);
    }

    public String getAIAdvice(int userId) {
        List<Transaction> list = transactionDAO.getTransactionsByUser(userId);
        if (list.isEmpty()) {
            return "No transactions found. Add some transactions first!";
        }
        StringBuilder summary = new StringBuilder();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : list) {
            summary.append(t.getType()).append(": ")
                   .append(t.getCategory()).append(" - Rs.")
                   .append(t.getAmount()).append(" - ")
                   .append(t.getDescription()).append("\n");
            if (t.getType().equals("INCOME")) {
                totalIncome = totalIncome.add(t.getAmount());
            } else {
                totalExpense = totalExpense.add(t.getAmount());
            }
        }
        summary.append("\nTotal Income: Rs.").append(totalIncome);
        summary.append("\nTotal Expense: Rs.").append(totalExpense);
        summary.append("\nNet Savings: Rs.").append(totalIncome.subtract(totalExpense));

        AIAdvisor advisor = new AIAdvisor();
        return advisor.getFinancialAdvice(summary.toString());
    }
}