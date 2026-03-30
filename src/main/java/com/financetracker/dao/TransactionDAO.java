package com.financetracker.dao;

import com.financetracker.DBConnection;
import com.financetracker.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // SAVE a new transaction to DB
    public boolean addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (user_id, type, category, amount, description, date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, t.getUserId());
            stmt.setString(2, t.getType());
            stmt.setString(3, t.getCategory());
            stmt.setBigDecimal(4, t.getAmount());
            stmt.setString(5, t.getDescription());
            stmt.setDate(6, Date.valueOf(t.getDate())); // LocalDate → SQL Date

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
            return false;
        }
    }

    // FETCH all transactions for a specific user
    public List<Transaction> getTransactionsByUser(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            // Loop through every row returned
            while (rs.next()) {
                Transaction t = new Transaction(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("type"),
                    rs.getString("category"),
                    rs.getBigDecimal("amount"),
                    rs.getString("description"),
                    rs.getDate("date").toLocalDate() // SQL Date → LocalDate
                );
                transactions.add(t);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }

    // FETCH monthly summary — total income and expense for a month
    public void getMonthlySummary(int userId, int month, int year) {
        String sql = "SELECT type, SUM(amount) as total FROM transactions " +
                     "WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ? " +
                     "GROUP BY type";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);

            ResultSet rs = stmt.executeQuery();
            System.out.println("\n--- Monthly Summary ---");
            while (rs.next()) {
                System.out.printf("%s: Rs.%.2f%n",
                    rs.getString("type"),
                    rs.getBigDecimal("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error getting summary: " + e.getMessage());
        }
    }
}