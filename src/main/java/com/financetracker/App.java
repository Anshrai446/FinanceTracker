package com.financetracker;

import com.financetracker.models.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.FinanceService;

import java.time.LocalDate;
import java.util.Scanner;

public class App {

    private static User loggedInUser = null;
    private static AuthService authService = new AuthService();
    private static FinanceService financeService = new FinanceService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("   AI Finance Tracker v1.0     ");
        System.out.println("================================");

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
            case "3" -> { System.out.println("Goodbye!"); System.exit(0); }
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\nWelcome, " + loggedInUser.getName() + "!");
        System.out.println("1. Add Transaction");
        System.out.println("2. View All Transactions");
        System.out.println("3. Monthly Summary");
        System.out.println("4. AI Advisor 🤖");
        System.out.println("5. Logout");
        System.out.print("Choose: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> handleAddTransaction();
            case "2" -> financeService.viewTransactions(loggedInUser.getId());
            case "3" -> handleMonthlySummary();
            case "4" -> {
    System.out.println("\n🤖 Consulting AI Advisor...\n");
    String advice = financeService.getAIAdvice(loggedInUser.getId());
    System.out.println("💡 AI Advisor Says:");
    System.out.println("─────────────────────────────");
    System.out.println(advice);
    System.out.println("─────────────────────────────");
}
            case "5" -> { loggedInUser = null; System.out.println("Logged out."); }
            default -> System.out.println("Invalid choice.");
        }
    }

   private static void handleRegister() {
    System.out.print("Name: ");
    String name = scanner.nextLine();
    System.out.print("Email: ");
    String email = scanner.nextLine();
    String password = readPassword("Password: ");
    authService.register(name, email, password);
}

private static void handleLogin() {
    System.out.print("Email: ");
    String email = scanner.nextLine();
    String password = readPassword("Password: ");
    loggedInUser = authService.login(email, password);
}

    private static void handleAddTransaction() {
        System.out.print("Type (INCOME/EXPENSE): ");
        String type = scanner.nextLine();
        System.out.print("Category (Food/Salary/Transport etc): ");
        String category = scanner.nextLine();
        System.out.print("Amount: ");
        String amount = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Date (yyyy-MM-dd) or press Enter for today: ");
        String dateStr = scanner.nextLine();
        if (dateStr.trim().isEmpty()) {
            dateStr = LocalDate.now().toString();
        }
        financeService.addTransaction(
            loggedInUser.getId(), type, category, amount, description, dateStr);
    }

    private static void handleMonthlySummary() {
        System.out.print("Enter month (1-12): ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter year (e.g. 2026): ");
        int year = Integer.parseInt(scanner.nextLine());
        financeService.viewMonthlySummary(loggedInUser.getId(), month, year);
    }

private static String readPassword(String prompt) {
    System.out.print(prompt);

    // Console works in real terminal but not in VS Code
    java.io.Console console = System.console();

    if (console != null) {
        // Real terminal — password is hidden automatically
        char[] pwd = console.readPassword();
        return new String(pwd);
    } else {
        // VS Code terminal — give user the choice
        System.out.print("\n  (Press Enter to type openly, or run from cmd for hidden input): ");
        return scanner.nextLine();
    }
}
}