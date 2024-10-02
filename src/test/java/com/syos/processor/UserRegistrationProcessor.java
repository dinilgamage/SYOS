package com.syos.processor;

import com.syos.exception.UserAlreadyExistsException;
import com.syos.facade.StoreFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.mockito.Mockito.*;

class UserRegistrationProcessorTest {

  private StoreFacade mockStoreFacade;
  private UserRegistrationProcessor userRegistrationProcessor;
  private Scanner mockScanner;

  @BeforeEach
  void setUp() {
    mockStoreFacade = mock(StoreFacade.class);
    mockScanner = mock(Scanner.class);
    userRegistrationProcessor = new UserRegistrationProcessor(mockStoreFacade);
  }

  @Test
  public void testRegisterUser_SuccessfulRegistration() {
    // Arrange
    when(mockScanner.next()).thenReturn("John Doe", "johndoe@example.com", "Password123!");

    // Act
    userRegistrationProcessor.registerUser(mockScanner);

    // Assert
    verify(mockStoreFacade).registerUser("John Doe", "johndoe@example.com", "Password123!");
    verifyNoMoreInteractions(mockStoreFacade);
  }

  @Test
  public void testRegisterUser_UserAlreadyExists() {
    // Arrange
    when(mockScanner.next()).thenReturn("John Doe", "johndoe@example.com", "Password123!");

    // Mock throwing UserAlreadyExistsException
    doThrow(new UserAlreadyExistsException("User already exists"))
      .when(mockStoreFacade).registerUser("John Doe", "johndoe@example.com", "Password123!");

    // Act
    userRegistrationProcessor.registerUser(mockScanner);

    // Assert
    verify(mockStoreFacade).registerUser("John Doe", "johndoe@example.com", "Password123!");
    verifyNoMoreInteractions(mockStoreFacade);
  }
}
