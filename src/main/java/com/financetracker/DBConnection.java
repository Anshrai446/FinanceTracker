package com.financetracker;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    // This holds our single connection instance
    private static Connection connection = null;

    // Private constructor — nobody can do "new DBConnection()"
    private DBConnection() {}

    public static Connection getConnection() {
        try {
            // Only create connection if it doesn't exist yet
            if (connection == null || connection.isClosed()) {

                // Load config.properties file from resources folder
                Properties props = new Properties();
                InputStream input = DBConnection.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

                if (input == null) {
                    System.out.println("config.properties file not found!");
                    return null;
                }

                props.load(input);

                // Read the three values from the file
                String url = props.getProperty("db.url");
                String username = props.getProperty("db.username");
                String password = props.getProperty("db.password");

                // Create the actual connection to MySQL
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Database connected successfully!");
            }
        } catch (SQLException | IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return connection;
    }
}