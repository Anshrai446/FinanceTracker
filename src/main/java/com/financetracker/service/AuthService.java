package com.financetracker.service;

import com.financetracker.dao.UserDAO;
import com.financetracker.models.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    // UserDAO is our bridge to the database
    private UserDAO userDAO = new UserDAO();

    public boolean register(String name, String email, String password) {

        // Basic validation — don't even hit the DB if input is wrong
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return false;
        }
        if (email == null || !email.contains("@")) {
            System.out.println("Please enter a valid email.");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters.");
            return false;
        }

        // Create User object and send to DAO
        User user = new User(name.trim(), email.trim().toLowerCase(), password);
        boolean success = userDAO.registerUser(user);

        if (success) {
            System.out.println("Registration successful! Welcome, " + name + "!");
        }
        return success;
    }

    public User login(String email, String password) {

        if (email == null || password == null) {
            System.out.println("Email and password are required.");
            return null;
        }

        // Fetch user from DB by email
        User user = userDAO.getUserByEmail(email.trim().toLowerCase());

        if (user == null) {
            System.out.println("No account found with this email.");
            return null;
        }

        // BCrypt.checkpw compares plain password with hashed password
        if (BCrypt.checkpw(password, user.getPassword())) {
            System.out.println("Login successful! Welcome back, " + user.getName() + "!");
            return user;
        } else {
            System.out.println("Incorrect password. Please try again.");
            return null;
        }
    }
}