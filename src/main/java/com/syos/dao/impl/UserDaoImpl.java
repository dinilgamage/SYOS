package com.syos.dao.impl;

import com.syos.dao.UserDao;
import com.syos.database.DatabaseConnection;
import com.syos.model.User;

import java.sql.*;

public class UserDaoImpl implements UserDao {

  private static final String INSERT_USER_SQL = "INSERT INTO Users (name, email, password) VALUES (?, ?, ?)";
  private static final String SELECT_USER_BY_EMAIL = "SELECT * FROM Users WHERE email = ?";
  private static final String VERIFY_USER_CREDENTIALS_SQL = "SELECT * FROM Users WHERE email = ? AND password = ?";

  @Override
  public void saveUser(User user) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {

      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getPassword()); // Assuming password is hashed before saving

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle exceptions properly (logging or rethrowing)
    }
  }

  @Override
  public User getUserByEmail(String email) {
    User user = null;

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_EMAIL)) {

      preparedStatement.setString(1, email);
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        user = mapRowToUser(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle exceptions properly (logging or rethrowing)
    }
    return user;
  }

  @Override
  public boolean verifyUserCredentials(String email, String password) {
    boolean isValid = false;

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(VERIFY_USER_CREDENTIALS_SQL)) {

      preparedStatement.setString(1, email);
      preparedStatement.setString(2, password); // Assuming password is already hashed before passing to this method
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        isValid = true; // Credentials match
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle exceptions properly (logging or rethrowing)
    }
    return isValid;
  }

  // Helper method to map a ResultSet row to a User object
  private User mapRowToUser(ResultSet rs) throws SQLException {
    int userId = rs.getInt("user_id");
    String name = rs.getString("name");
    String email = rs.getString("email");
    String password = rs.getString("password"); // Password should be hashed

    User user = new User(name, email, password);
    user.setUserId(userId);
    return user;
  }
}

