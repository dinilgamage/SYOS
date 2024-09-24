package com.syos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

  // JDBC connection credentials
  private static final String URL = "jdbc:mysql://localhost:3306/syos_db"; // Change as per your database
  private static final String USERNAME = "root"; // Your database username
  private static final String PASSWORD = "mysql123"; // Your database password

  // Private constructor to prevent instantiation
  private DatabaseConnection() {
    // Private constructor to prevent instantiation
  }

  // Public method to get a new database connection
  public static Connection getConnection() {
    try {
      return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Error connecting to the database", e);
    }
  }
}
