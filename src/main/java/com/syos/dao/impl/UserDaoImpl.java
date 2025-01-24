package com.syos.dao.impl;

import com.syos.dao.UserDao;
import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.model.User;

import java.sql.*;

public class UserDaoImpl implements UserDao {

  private static final String INSERT_USER_SQL = "INSERT INTO User (name, email, password) VALUES (?, ?, ?)";
  private static final String SELECT_USER_BY_EMAIL = "SELECT * FROM User WHERE email = ?";
  private static final String VERIFY_USER_CREDENTIALS_SQL = "SELECT * FROM User WHERE email = ? AND password = ?";
  private static final String REGISTER_USER_SQL = "INSERT INTO User (name, email, password) VALUES (?, ?, ?)";

  @Override
  public void saveUser(User user) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {

      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getPassword());

      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected == 0) {
        throw new DaoException("User save failed, no rows affected for email: " + user.getEmail());
      }

    } catch (SQLException e) {
      throw new DaoException("Error saving user with email: " + user.getEmail(), e);
    }
  }

  @Override
  public int registerUser(User user) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(REGISTER_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getPassword());

      int rowsInserted = preparedStatement.executeUpdate();

      if (rowsInserted > 0) {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
          } else {
            throw new SQLException("Creating user failed, no ID obtained.");
          }
        }
      } else {
        throw new SQLException("Creating user failed, no rows affected.");
      }

    } catch (SQLException e) {
      throw new DaoException("Error registering user with email: " + user.getEmail(), e);
    }
  }

  @Override
  public User getUserByEmail(String email) {
    User user = null;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_EMAIL)) {

      preparedStatement.setString(1, email);
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        user = mapRowToUser(rs);
      }

    } catch (SQLException e) {
      throw new DaoException("Error retrieving user with email: " + email, e);
    }
    return user;
  }

  @Override
  public boolean verifyUserCredentials(String email, String password) {
    System.out.println("INFO: LoginServlet invoked by user: "+ email);
    boolean isValid = false;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(VERIFY_USER_CREDENTIALS_SQL)) {

      preparedStatement.setString(1, email);
      preparedStatement.setString(2, password);
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        isValid = true;
      }

    } catch (SQLException e) {
      throw new DaoException("Error verifying user credentials for email: " + email, e);
    }
    return isValid;
  }

  // Helper method to map a ResultSet row to a User object
  private User mapRowToUser(ResultSet rs) throws SQLException {
    int userId = rs.getInt("user_id");
    String name = rs.getString("name");
    String email = rs.getString("email");
    String password = rs.getString("password");

    User user = new User(name, email, password);
    user.setUserId(userId);
    return user;
  }
}
