package com.syos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

  // JDBC connection credentials
  private static final String URL = "jdbc:mysql://localhost:3306/syos_db";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "mysql123";

  private DatabaseConnection() {
    // Private constructor to prevent instantiation
  }

  public static Connection getConnection() {
    try {
      return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Error connecting to the database", e);
    }
  }
}
