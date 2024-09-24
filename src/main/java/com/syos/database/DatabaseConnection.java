package com.syos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

  // Singleton instance
  private static DatabaseConnection instance;

  // JDBC connection object
  private Connection connection;

  // Database connection credentials
  private static final String URL = "jdbc:mysql://localhost:3306/syos_db"; // Change as per your database
  private static final String USERNAME = "root"; // Your database username
  private static final String PASSWORD = "mysql123"; // Your database password

  // Private constructor to prevent instantiation
  private DatabaseConnection() {
    try {
      connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    } catch (SQLException e) {
      e.printStackTrace();
      // Log or throw a runtime exception if the connection fails
      throw new RuntimeException("Error connecting to the database", e);
    }
  }

  // Public static method to get the single instance of DatabaseConnection
  public static DatabaseConnection getInstance() {
    if (instance == null) {
      synchronized (DatabaseConnection.class) {
        if (instance == null) {
          instance = new DatabaseConnection();
        }
      }
    }
    return instance;
  }

  // Method to get the connection object
  public Connection getConnection() {
    return connection;
  }
}

