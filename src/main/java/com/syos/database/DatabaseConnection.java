package com.syos.database;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

  private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

  private static final String URL = "jdbc:mysql://localhost:3306/syos_db_dinil";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "mysql123";
  private static final int MAX_POOL_SIZE = 20;
  private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

  private static final int TIMEOUT = 5000;
  private static final String CLOSE_METHOD_NAME = "close";

  private static final LinkedList<Connection> connectionPool = new LinkedList<>();

  static {
    initializeConnectionPool();
  }

  private static void initializeConnectionPool() {
    try {
      Class.forName(DRIVER_CLASS_NAME);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("MySQL JDBC Driver not found. Ensure it's in your classpath.", e);
    }

    for (int i = 0; i < MAX_POOL_SIZE; i++) {
      try {
        connectionPool.add(createNewConnection());
      } catch (SQLException e) {
        logger.log(Level.WARNING, "Failed to create connection #" + (i + 1) + ": " + e.getMessage(), e);
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
    long startTime = System.currentTimeMillis();

    while (connectionPool.isEmpty()) {
      long elapsedTime = System.currentTimeMillis() - startTime;
      long remainingTime = TIMEOUT - elapsedTime;

      if (remainingTime <= 0) {
        throw new RuntimeException("Timeout waiting for a database connection");
      }

      try {
        DatabaseConnection.class.wait(remainingTime);
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
        if (CLOSE_METHOD_NAME.equals(method.getName())) {
          releaseConnection(realConnection);
          return null;
        }
        return method.invoke(realConnection, args);
      });
  }

  public static synchronized void releaseConnection(Connection connection) {
    if (Objects.nonNull(connection)) {
      connectionPool.addLast(connection);
      DatabaseConnection.class.notifyAll();
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
