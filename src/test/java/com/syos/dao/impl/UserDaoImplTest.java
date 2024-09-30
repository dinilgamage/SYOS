package com.syos.dao.impl;

import com.syos.dao.impl.UserDaoImpl;
import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDaoImplTest {

  private UserDaoImpl userDao;
  private Connection mockConnection;
  private PreparedStatement mockPreparedStatement;
  private ResultSet mockResultSet;
  private MockedStatic<DatabaseConnection> mockedDatabaseConnection;

  @BeforeEach
  public void setUp() throws Exception {
    // Mock the database connection, prepared statement, and result set
    mockConnection = mock(Connection.class);
    mockPreparedStatement = mock(PreparedStatement.class);
    mockResultSet = mock(ResultSet.class);

    // Mock the DatabaseConnection to return the mocked connection
    mockedDatabaseConnection = mockStatic(DatabaseConnection.class);
    mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

    userDao = new UserDaoImpl();
  }

  @AfterEach
  public void tearDown() {
    // Close the mocked static after each test to avoid leakage
    mockedDatabaseConnection.close();
  }

  @Test
  public void testSaveUser_Success() throws Exception {
    // Arrange
    User user = new User("John Doe", "john@example.com", "hashedpassword");
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    // Simulate a successful save with 1 row affected
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);

    // Act
    userDao.saveUser(user);

    // Assert
    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testSaveUser_NoRowsAffected() throws Exception {
    // Arrange
    User user = new User("John Doe", "john@example.com", "hashedpassword");
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    // Simulate no rows affected during insert
    when(mockPreparedStatement.executeUpdate()).thenReturn(0);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      userDao.saveUser(user);
    });

    assertEquals("User save failed, no rows affected for email: john@example.com", thrown.getMessage());
    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testSaveUser_SQLException() throws Exception {
    // Arrange
    User user = new User("John Doe", "john@example.com", "hashedpassword");
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    // Simulate an SQLException
    when(mockPreparedStatement.executeUpdate()).thenThrow(SQLException.class);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      userDao.saveUser(user);
    });

    assertTrue(thrown.getMessage().contains("Error saving user with email: john@example.com"));
    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testGetUserByEmail_Success() throws Exception {
    // Arrange
    String email = "john@example.com";
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Simulate finding a user
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt("user_id")).thenReturn(1);
    when(mockResultSet.getString("name")).thenReturn("John Doe");
    when(mockResultSet.getString("email")).thenReturn(email);
    when(mockResultSet.getString("password")).thenReturn("hashedpassword");

    // Act
    User user = userDao.getUserByEmail(email);

    // Assert
    assertNotNull(user);
    assertEquals(1, user.getUserId());
    assertEquals("John Doe", user.getName());
    assertEquals(email, user.getEmail());
    assertEquals("hashedpassword", user.getPassword());

    // Verify that correct queries were executed
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockPreparedStatement, times(1)).setString(1, email);
  }

  @Test
  public void testGetUserByEmail_NoResult() throws Exception {
    // Arrange
    String email = "john@example.com";
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Simulate no user found
    when(mockResultSet.next()).thenReturn(false);

    // Act
    User user = userDao.getUserByEmail(email);

    // Assert
    assertNull(user);

    // Verify that correct queries were executed
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockPreparedStatement, times(1)).setString(1, email);
  }

  @Test
  public void testGetUserByEmail_SQLException() throws Exception {
    // Arrange
    String email = "john@example.com";
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    // Simulate an SQLException
    when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      userDao.getUserByEmail(email);
    });

    assertTrue(thrown.getMessage().contains("Error retrieving user with email: john@example.com"));
    verify(mockPreparedStatement, times(1)).executeQuery();
  }

  @Test
  public void testVerifyUserCredentials_Success() throws Exception {
    // Arrange
    String email = "john@example.com";
    String password = "hashedpassword";
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Simulate successful credentials check
    when(mockResultSet.next()).thenReturn(true);

    // Act
    boolean isValid = userDao.verifyUserCredentials(email, password);

    // Assert
    assertTrue(isValid);

    // Verify that correct queries were executed
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockPreparedStatement, times(1)).setString(1, email);
    verify(mockPreparedStatement, times(1)).setString(2, password);
  }

  @Test
  public void testVerifyUserCredentials_Invalid() throws Exception {
    // Arrange
    String email = "john@example.com";
    String password = "wrongpassword";
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Simulate failed credentials check
    when(mockResultSet.next()).thenReturn(false);

    // Act
    boolean isValid = userDao.verifyUserCredentials(email, password);

    // Assert
    assertFalse(isValid);

    // Verify that correct queries were executed
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockPreparedStatement, times(1)).setString(1, email);
    verify(mockPreparedStatement, times(1)).setString(2, password);
  }

  @Test
  public void testVerifyUserCredentials_SQLException() throws Exception {
    // Arrange
    String email = "john@example.com";
    String password = "hashedpassword";
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    // Simulate an SQLException
    when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      userDao.verifyUserCredentials(email, password);
    });

    assertTrue(thrown.getMessage().contains("Error verifying user credentials for email: john@example.com"));
    verify(mockPreparedStatement, times(1)).executeQuery();
  }
}
