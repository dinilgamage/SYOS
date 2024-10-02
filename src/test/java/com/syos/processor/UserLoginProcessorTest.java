package com.syos.processor;

import com.syos.facade.StoreFacade;
import com.syos.processor.UserLoginProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserLoginProcessorTest {

  private StoreFacade mockStoreFacade;
  private Scanner mockScanner;
  private UserLoginProcessor userLoginProcessor;

  @BeforeEach
  public void setUp() {
    mockStoreFacade = mock(StoreFacade.class);
    mockScanner = mock(Scanner.class);
    userLoginProcessor = new UserLoginProcessor(mockStoreFacade);
  }

  @Test
  public void testLoginUser_SuccessfulLogin() {
    // Arrange
    when(mockScanner.next()).thenReturn("test@example.com", "password123");
    when(mockStoreFacade.loginUser("test@example.com", "password123")).thenReturn(true);

    // Act
    String result = userLoginProcessor.loginUser(mockScanner);

    // Assert
    assertEquals("test@example.com", result);
  }

  @Test
  public void testLoginUser_InvalidCredentials() {
    // Arrange
    when(mockScanner.next()).thenReturn("wrong@example.com", "wrongpassword");
    when(mockStoreFacade.loginUser("wrong@example.com", "wrongpassword")).thenReturn(false);

    // Act
    String result = userLoginProcessor.loginUser(mockScanner);

    // Assert
    assertNull(result);
  }

  @Test
  public void testLoginUser_EmptyEmail() {
    // Arrange
    when(mockScanner.next()).thenReturn("", "password123");
    when(mockStoreFacade.loginUser("", "password123")).thenReturn(false);

    // Act
    String result = userLoginProcessor.loginUser(mockScanner);

    // Assert
    assertNull(result);
  }

  @Test
  public void testLoginUser_EmptyPassword() {
    // Arrange
    when(mockScanner.next()).thenReturn("test@example.com", "");
    when(mockStoreFacade.loginUser("test@example.com", "")).thenReturn(false);

    // Act
    String result = userLoginProcessor.loginUser(mockScanner);

    // Assert
    assertNull(result);
  }

  @Test
  public void testLoginUser_InvalidEmailFormat() {
    // Arrange
    when(mockScanner.next()).thenReturn("invalidEmailFormat", "password123");
    when(mockStoreFacade.loginUser("invalidEmailFormat", "password123")).thenReturn(false);

    // Act
    String result = userLoginProcessor.loginUser(mockScanner);

    // Assert
    assertNull(result);
  }

  @Test
  public void testLoginUser_CaseSensitiveCheck() {
    // Arrange
    when(mockScanner.next()).thenReturn("Test@Example.com", "Password123");
    when(mockStoreFacade.loginUser("Test@Example.com", "Password123")).thenReturn(true);

    // Act
    String result = userLoginProcessor.loginUser(mockScanner);

    // Assert
    assertEquals("Test@Example.com", result);
  }

  @Test
  public void testLoginUser_CaseSensitiveEmailFails() {
    // Arrange
    when(mockScanner.next()).thenReturn("test@example.com", "password123");
    when(mockStoreFacade.loginUser("Test@example.com", "password123")).thenReturn(false);

    // Act
    String result = userLoginProcessor.loginUser(mockScanner);

    // Assert
    assertNull(result);
  }

  @Test
  public void testLoginUser_CaseSensitivePasswordFails() {
    // Arrange
    when(mockScanner.next()).thenReturn("test@example.com", "password123");
    when(mockStoreFacade.loginUser("test@example.com", "Password123")).thenReturn(false);

    // Act
    String result = userLoginProcessor.loginUser(mockScanner);

    // Assert
    assertNull(result);
  }
}
