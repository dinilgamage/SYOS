package com.syos.service;

import com.syos.dao.UserDao;
import com.syos.exception.UserAlreadyExistsException;
import com.syos.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

  private UserDao mockUserDao;
  private UserService userService;

  @BeforeEach
  public void setUp() {
    mockUserDao = mock(UserDao.class);
    userService = new UserService(mockUserDao);
  }

  /**
   * Test registering a new user (Happy Path).
   */
  @Test
  public void testRegisterUser_Success() {
    // Arrange
    User newUser = new User("John Doe", "john.doe@example.com", "hashedPassword");

    // Mock the UserDao to return null when checking for existing email
    when(mockUserDao.getUserByEmail("john.doe@example.com")).thenReturn(null);

    // Act
    userService.registerUser(newUser);

    // Assert
    verify(mockUserDao, times(1)).saveUser(newUser);
  }

  /**
   * Test registering a user with an email that already exists (Edge Case).
   */
  @Test
  public void testRegisterUser_EmailAlreadyExists() {
    // Arrange
    User existingUser = new User("Jane Doe", "jane.doe@example.com", "hashedPassword");

    // Mock the UserDao to return an existing user
    when(mockUserDao.getUserByEmail("jane.doe@example.com")).thenReturn(existingUser);

    // Act & Assert
    assertThrows(UserAlreadyExistsException.class, () -> {
      userService.registerUser(existingUser);
    });

    // Verify that saveUser is never called when the email is already registered
    verify(mockUserDao, never()).saveUser(existingUser);
  }

  /**
   * Test validating user login (Happy Path).
   */
  @Test
  public void testLoginUser_Success() {
    // Arrange
    String email = "john.doe@example.com";
    String password = "hashedPassword";

    // Mock the UserDao to return true when credentials are valid
    when(mockUserDao.verifyUserCredentials(email, password)).thenReturn(true);

    // Act
    boolean result = userService.loginUser(email, password);

    // Assert
    assertTrue(result);
    verify(mockUserDao, times(1)).verifyUserCredentials(email, password);
  }

  /**
   * Test validating user login with incorrect credentials (Edge Case).
   */
  @Test
  public void testLoginUser_IncorrectCredentials() {
    // Arrange
    String email = "john.doe@example.com";
    String password = "wrongPassword";

    // Mock the UserDao to return false when credentials are invalid
    when(mockUserDao.verifyUserCredentials(email, password)).thenReturn(false);

    // Act
    boolean result = userService.loginUser(email, password);

    // Assert
    assertFalse(result);
    verify(mockUserDao, times(1)).verifyUserCredentials(email, password);
  }

  /**
   * Test retrieving a user by email (Happy Path).
   */
  @Test
  public void testGetUserByEmail_Success() {
    // Arrange
    User existingUser = new User("John Doe", "john.doe@example.com", "hashedPassword");

    // Mock the UserDao to return the user when email is found
    when(mockUserDao.getUserByEmail("john.doe@example.com")).thenReturn(existingUser);

    // Act
    User result = userService.getUserByEmail("john.doe@example.com");

    // Assert
    assertNotNull(result);
    assertEquals("John Doe", result.getName());
    assertEquals("john.doe@example.com", result.getEmail());
    verify(mockUserDao, times(1)).getUserByEmail("john.doe@example.com");
  }

  /**
   * Test retrieving a user by email when the user does not exist (Edge Case).
   */
  @Test
  public void testGetUserByEmail_UserNotFound() {
    // Arrange
    String email = "unknown@example.com";

    // Mock the UserDao to return null when the email is not found
    when(mockUserDao.getUserByEmail(email)).thenReturn(null);

    // Act
    User result = userService.getUserByEmail(email);

    // Assert
    assertNull(result);
    verify(mockUserDao, times(1)).getUserByEmail(email);
  }
}
