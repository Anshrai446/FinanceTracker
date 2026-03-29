package com.financetracker;

import com.financetracker.models.User;
import com.financetracker.service.AuthService;

import java.util.Scanner;

public class App {

    // loggedInUser holds the current session — null means nobody is logged in
    private static User loggedInUser = null;
    private static AuthService authService = new AuthService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("   AI Finance Tracker v1.0     ");
        System.out.println("================================");

        // Keep showing menu until user exits
        while (true) {
            if (loggedInUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showAuthMenu() {
        System.out.println("\n1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> handleRegister();
            case "2" -> handleLogin();
            case "3" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice. Try again.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\nWelcome, " + loggedInUser.getName() + "!");
        System.out.println("1. Add Transaction (Coming Day 3)");
        System.out.println("2. View Transactions (Coming Day 4)");
        System.out.println("3. AI Advisor (Coming Day 7)");
        System.out.println("4. Logout");
        System.out.print("Choose: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> System.out.println("Coming on Day 3!");
            case "2" -> System.out.println("Coming on Day 4!");
            case "3" -> System.out.println("Coming on Day 7!");
            case "4" -> {
                loggedInUser = null;
                System.out.println("Logged out successfully.");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void handleRegister() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        authService.register(name, email, password);
    }

    private static void handleLogin() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        loggedInUser = authService.login(email, password);
    }
}