package com.syos.processor;

import com.syos.enums.TransactionType;
import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;
import com.syos.processor.BillingProcessor;
import com.syos.processor.UserMenuProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class UserMenuProcessorTest {

  private StoreFacade mockStoreFacade;
  private BillingProcessor mockBillingProcessor;
  private UserMenuProcessor userMenuProcessor;
  private Scanner mockScanner;

  @BeforeEach
  public void setUp() {
    mockStoreFacade = mock(StoreFacade.class);
    mockBillingProcessor = mock(BillingProcessor.class);
    mockScanner = mock(Scanner.class);
    userMenuProcessor = new UserMenuProcessor(mockStoreFacade, mockBillingProcessor);
  }

  // Test case for displaying the user menu and selecting the logout option
  @Test
  public void testDisplayUserMenu_LogoutOption() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(2); // User chooses to log out

    // Act
    userMenuProcessor.displayUserMenu(mockScanner, "test@example.com");

    // Assert
    verify(mockStoreFacade, never()).getAllItems(); // No items should be fetched
    verify(mockBillingProcessor, never()).processBilling(any(), any(), any()); // Billing should not be processed
    verify(mockScanner, times(1)).nextInt(); // Make sure user input was taken once
  }

  // Test case for making a purchase
  @Test
  public void testDisplayUserMenu_MakePurchase() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(1, 2); // User chooses to make a purchase, then log out
    when(mockStoreFacade.getAllItems()).thenReturn(Arrays.asList(
      new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100),
      new Inventory("ITEM002", "Item Two", new BigDecimal("200.00"), 20, 15, 100)
                                                                ));
    when(mockStoreFacade.getUserId("test@example.com")).thenReturn(1);

    // Act
    userMenuProcessor.displayUserMenu(mockScanner, "test@example.com");

    // Assert
    verify(mockStoreFacade, times(1)).getAllItems(); // Inventory should be fetched once
    verify(mockBillingProcessor).processBilling(mockScanner, TransactionType.ONLINE, 1); // Billing should be triggered
  }

  // Test case for displaying invalid menu option
  @Test
  public void testDisplayUserMenu_InvalidOption() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(99, 2); // User chooses invalid option, then log out

    // Act
    userMenuProcessor.displayUserMenu(mockScanner, "test@example.com");

    // Assert
    verify(mockStoreFacade, never()).getAllItems(); // Inventory should not be fetched on invalid input
    verify(mockBillingProcessor, never()).processBilling(any(), any(), any()); // Billing should not be triggered
  }

  // Test case for displaying available inventory
  @Test
  public void testDisplayInventoryWithOnlineStock() {
    // Arrange: Mock the inventory to be displayed
    when(mockStoreFacade.getAllItems()).thenReturn(Arrays.asList(
      new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100),
      new Inventory("ITEM002", "Item Two", new BigDecimal("200.00"), 20, 15, 100)
                                                                ));

    // Mock scanner to first select option 1 (Make Purchase), then 2 (Logout)
    when(mockScanner.nextInt()).thenReturn(1, 2); // User chooses option 1 (make purchase) first, then option 2 (logout)

    // Act: Call the user menu
    userMenuProcessor.displayUserMenu(mockScanner, "test@example.com");

    // Assert: Ensure the inventory is fetched and displayed
    verify(mockStoreFacade, times(1)).getAllItems(); // Ensure inventory is fetched once
  }
}
