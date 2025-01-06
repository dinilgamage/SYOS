package com.syos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

  // JDBC connection credentials
  private static final String URL = "jdbc:mysql://localhost:3306/syos_db_dinil";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "mysql123";

  private DatabaseConnection() {
    // Private constructor to prevent instantiation
  }

  public static Connection getConnection() {
    try {
      // Explicitly load the MySQL JDBC driver
      Class.forName("com.mysql.cj.jdbc.Driver");
      return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("MySQL JDBC Driver not found. Make sure mysql-connector-java is in your classpath.", e);
    } catch (SQLException e) {
      throw new RuntimeException("Error connecting to the database. Check the database URL, username, and password.", e);
    }
  }
}
