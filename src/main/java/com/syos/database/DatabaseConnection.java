package com.syos.database;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Objects;

public class DatabaseConnection {

  private static final String URL = "jdbc:mysql://localhost:3306/syos_db_dinil";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "mysql123";
  private static final int MAX_POOL_SIZE = 20;

  private static final LinkedList<Connection> connectionPool = new LinkedList<>();

  static {
    initializeConnectionPool();
  }

  private static void initializeConnectionPool() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure driver is loaded
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("MySQL JDBC Driver not found. Ensure it's in your classpath.", e);
    }

    for (int i = 0; i < MAX_POOL_SIZE; i++) {
      try {
        connectionPool.add(createNewConnection());
      } catch (SQLException e) {
        System.err.println("Warning: Failed to create connection #" + (i + 1) + ": " + e.getMessage());
      }
    }

    if (connectionPool.isEmpty()) {
      throw new RuntimeException("Failed to initialize the connection pool: no connections available.");
    }
  }

  private static Connection createNewConnection() throws SQLException {
    return DriverManager.getConnection(URL, USERNAME, PASSWORD);
  }

  public static synchronized Connection getConnection() {
    while (connectionPool.isEmpty()) {
      try {
        DatabaseConnection.class.wait(); // Wait if no connections are available
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Thread interrupted while waiting for a connection", e);
      }
    }
    Connection realConnection = connectionPool.removeFirst();
    return createProxyConnection(realConnection);
  }

  private static Connection createProxyConnection(Connection realConnection) {
    return (Connection) Proxy.newProxyInstance(
      DatabaseConnection.class.getClassLoader(),
      new Class<?>[]{Connection.class},
      (proxy, method, args) -> {
        if ("close".equals(method.getName())) {
          // Intercept the close method to return the connection to the pool
          releaseConnection(realConnection);
          return null;
        }
        // For all other methods, call the actual method on the real connection
        return method.invoke(realConnection, args);
      }
                                              );
  }

  public static synchronized void releaseConnection(Connection connection) {
    if (Objects.nonNull(connection)) {
      connectionPool.addLast(connection);
      DatabaseConnection.class.notifyAll(); // Notify waiting threads
    }
  }

  public static synchronized void shutdown() {
    for (Connection connection : connectionPool) {
      try {
        connection.close();
      } catch (SQLException e) {
        System.err.println("Error closing connection: " + e.getMessage());
      }
    }
    connectionPool.clear();
  }
}
