package com.financetracker.dao;

import com.financetracker.DBConnection;
import com.financetracker.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDAO {

    // SAVE a new user to the database
    public boolean registerUser(User user) {
        // SQL query with ? placeholders — never put values directly in SQL
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Hash the password before saving — NEVER store plain text passwords
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            // Fill in the ? placeholders in order
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashedPassword);

            // Execute the query
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // true if user was saved

        } catch (SQLIntegrityConstraintViolationException e) {
            // This triggers when email already exists (UNIQUE constraint)
            System.out.println("Email already registered. Please use a different email.");
            return false;
        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    // FETCH a user from database by email
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            // executeQuery returns a ResultSet — like a table of results
            ResultSet rs = stmt.executeQuery();

            // rs.next() moves to first row — if true, user was found
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        return null; // user not found
    }
}