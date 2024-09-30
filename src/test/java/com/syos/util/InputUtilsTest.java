package com.syos.util;

import com.syos.enums.DiscountType;
import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InputUtilsTest {

  private StoreFacade mockStoreFacade;
  private Scanner mockScanner;

  @BeforeEach
  public void setUp() {
    mockStoreFacade = mock(StoreFacade.class);
    mockScanner = mock(Scanner.class);
  }

  @AfterEach
  public void tearDown() {
    Mockito.clearAllCaches();
  }

  /**
   * Helper method to create a sample Inventory.
   */
  private Inventory createInventory(String itemCode, String name, BigDecimal price) {
    return new Inventory(itemCode, name, price, 50, 30, 100);
  }

  /**
   * Test validating a positive integer input (Happy Path).
   */
  @Test
  public void testGetValidatedPositiveInt_Success() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(5);

    // Act
    int result = InputUtils.getValidatedPositiveInt(mockScanner, "Enter a number: ");

    // Assert
    assertEquals(5, result);
  }

  /**
   * Test when the input is a negative integer (Edge Case).
   */
  @Test
  public void testGetValidatedPositiveInt_NegativeNumber() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(-3).thenReturn(10);  // First negative, then valid

    // Act
    int result = InputUtils.getValidatedPositiveInt(mockScanner, "Enter a number: ");

    // Assert
    assertEquals(10, result);  // Should eventually return the valid input
  }

  /**
   * Test validating a positive BigDecimal input (Happy Path).
   */
  @Test
  public void testGetValidatedPositiveBigDecimal_Success() {
    // Arrange
    when(mockScanner.nextBigDecimal()).thenReturn(new BigDecimal("20.00"));

    // Act
    BigDecimal result = InputUtils.getValidatedPositiveBigDecimal(mockScanner, "Enter a decimal value: ");

    // Assert
    assertEquals(new BigDecimal("20.00"), result);
  }

  /**
   * Test when the input is a negative BigDecimal (Edge Case).
   */
  @Test
  public void testGetValidatedPositiveBigDecimal_NegativeValue() {
    // Arrange
    when(mockScanner.nextBigDecimal())
      .thenReturn(new BigDecimal("-10.00"))
      .thenReturn(new BigDecimal("25.50"));  // First negative, then valid

    // Act
    BigDecimal result = InputUtils.getValidatedPositiveBigDecimal(mockScanner, "Enter a decimal value: ");

    // Assert
    assertEquals(new BigDecimal("25.50"), result);
  }

  /**
   * Test validating an existing inventory item (Happy Path).
   */
  @Test
  public void testGetValidatedInventoryItem_Success() {
    // Arrange
    Inventory inventoryItem = createInventory("ITEM001", "Item One", new BigDecimal("100.00"));
    when(mockScanner.next()).thenReturn("ITEM001");
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);

    // Act
    Inventory result = InputUtils.getValidatedInventoryItem(mockStoreFacade, mockScanner, "Enter item code: ");

    // Assert
    assertNotNull(result);
    assertEquals("ITEM001", result.getItemCode());
  }

  /**
   * Test when the item does not exist (Edge Case).
   */
  @Test
  public void testGetValidatedInventoryItem_ItemNotFound() {
    // Arrange
    when(mockScanner.next()).thenReturn("ITEM999").thenReturn("ITEM001");  // First item doesn't exist
    when(mockStoreFacade.getItemByCode("ITEM999")).thenReturn(null);
    Inventory inventoryItem = createInventory("ITEM001", "Item One", new BigDecimal("100.00"));
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);

    // Act
    Inventory result = InputUtils.getValidatedInventoryItem(mockStoreFacade, mockScanner, "Enter item code: ");

    // Assert
    assertNotNull(result);
    assertEquals("ITEM001", result.getItemCode());
  }

  /**
   * Test validating the cash tendered, ensuring it's greater than or equal to the total amount (Happy Path).
   */
  @Test
  public void testGetValidatedCashTendered_Success() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    when(mockScanner.nextBigDecimal()).thenReturn(new BigDecimal("150.00"));

    // Act
    BigDecimal result = InputUtils.getValidatedCashTendered(mockScanner, "Enter cash tendered: ", totalAmount);

    // Assert
    assertEquals(new BigDecimal("150.00"), result);
  }

  /**
   * Test when the cash tendered is insufficient (Edge Case).
   */
  @Test
  public void testGetValidatedCashTendered_InsufficientAmount() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    when(mockScanner.nextBigDecimal())
      .thenReturn(new BigDecimal("50.00"))
      .thenReturn(new BigDecimal("120.00"));  // First insufficient, then valid

    // Act
    BigDecimal result = InputUtils.getValidatedCashTendered(mockScanner, "Enter cash tendered: ", totalAmount);

    // Assert
    assertEquals(new BigDecimal("120.00"), result);
  }

  /**
   * Test validating discount (Happy Path).
   */
  @Test
  public void testValidateDiscount_FixedDiscountSuccess() {
    // Arrange
    Inventory inventoryItem = createInventory("ITEM001", "Item One", new BigDecimal("100.00"));
    BigDecimal discountValue = new BigDecimal("10.00");

    // Act
    boolean result = InputUtils.validateDiscount(DiscountType.FIXED, discountValue, inventoryItem);

    // Assert
    assertTrue(result);
  }

  /**
   * Test when the discount exceeds the item price for a fixed discount (Edge Case).
   */
  @Test
  public void testValidateDiscount_FixedDiscountExceedsPrice() {
    // Arrange
    Inventory inventoryItem = createInventory("ITEM001", "Item One", new BigDecimal("100.00"));
    BigDecimal discountValue = new BigDecimal("150.00");

    // Act
    boolean result = InputUtils.validateDiscount(DiscountType.FIXED, discountValue, inventoryItem);

    // Assert
    assertFalse(result);
  }

  /**
   * Test validating a percentage discount (Happy Path).
   */
  @Test
  public void testValidateDiscount_PercentageDiscountSuccess() {
    // Arrange
    Inventory inventoryItem = createInventory("ITEM001", "Item One", new BigDecimal("100.00"));
    BigDecimal discountValue = new BigDecimal("10.00");

    // Act
    boolean result = InputUtils.validateDiscount(DiscountType.PERCENTAGE, discountValue, inventoryItem);

    // Assert
    assertTrue(result);
  }

  /**
   * Test when the percentage discount exceeds 100% (Edge Case).
   */
  @Test
  public void testValidateDiscount_PercentageDiscountExceeds100() {
    // Arrange
    Inventory inventoryItem = createInventory("ITEM001", "Item One", new BigDecimal("100.00"));
    BigDecimal discountValue = new BigDecimal("120.00");

    // Act
    boolean result = InputUtils.validateDiscount(DiscountType.PERCENTAGE, discountValue, inventoryItem);

    // Assert
    assertFalse(result);
  }

  /**
   * Test validating string input against valid options (Happy Path).
   */
  @Test
  public void testGetValidatedStringOption_Success() {
    // Arrange
    when(mockScanner.next()).thenReturn("yes");

    // Act
    String result = InputUtils.getValidatedStringOption(mockScanner, "Enter option: ", "yes", "no");

    // Assert
    assertEquals("yes", result);
  }

  /**
   * Test when invalid string option is provided (Edge Case).
   */
  @Test
  public void testGetValidatedStringOption_InvalidOption() {
    // Arrange
    when(mockScanner.next()).thenReturn("maybe").thenReturn("yes"); // First invalid, then valid

    // Act
    String result = InputUtils.getValidatedStringOption(mockScanner, "Enter option: ", "yes", "no");

    // Assert
    assertEquals("yes", result);
  }

  /**
   * Test validating LocalDate input (Happy Path).
   */
  @Test
  public void testGetValidatedDate_Success() {
    // Arrange
    when(mockScanner.next()).thenReturn("2024-09-30");

    // Act
    LocalDate result = InputUtils.getValidatedDate(mockScanner, "Enter a date: ");

    // Assert
    assertEquals(LocalDate.of(2024, 9, 30), result);
  }

  /**
   * Test when invalid date format is provided (Edge Case).
   */
  @Test
  public void testGetValidatedDate_InvalidDate() {
    // Arrange
    when(mockScanner.next()).thenReturn("invalid-date").thenReturn("2024-09-30");  // First invalid, then valid

    // Act
    LocalDate result = InputUtils.getValidatedDate(mockScanner, "Enter a date: ");

    // Assert
    assertEquals(LocalDate.of(2024, 9, 30), result);
  }

  /**
   * Test validating email input (Happy Path).
   */
  @Test
  public void testGetValidatedEmail_Success() {
    // Arrange
    when(mockScanner.next()).thenReturn("test@example.com");

    // Act
    String result = InputUtils.getValidatedEmail(mockScanner, "Enter your email: ");

    // Assert
    assertEquals("test@example.com", result);
  }

  /**
   * Test when invalid email format is provided (Edge Case).
   */
  @Test
  public void testGetValidatedEmail_InvalidEmail() {
    // Arrange
    when(mockScanner.next()).thenReturn("invalid-email").thenReturn("test@example.com");  // First invalid, then valid

    // Act
    String result = InputUtils.getValidatedEmail(mockScanner, "Enter your email: ");

    // Assert
    assertEquals("test@example.com", result);
  }

  /**
   * Test validating password input (Happy Path).
   */
  @Test
  public void testGetValidatedPassword_Success() {
    // Arrange
    when(mockScanner.next()).thenReturn("P@ssw0rd!");

    // Act
    String result = InputUtils.getValidatedPassword(mockScanner, "Enter your password: ");

    // Assert
    assertEquals("P@ssw0rd!", result);
  }

  /**
   * Test when invalid password is provided (Edge Case).
   */
  @Test
  public void testGetValidatedPassword_InvalidPassword() {
    // Arrange
    when(mockScanner.next()).thenReturn("weakpass").thenReturn("P@ssw0rd!");  // First invalid, then valid

    // Act
    String result = InputUtils.getValidatedPassword(mockScanner, "Enter your password: ");

    // Assert
    assertEquals("P@ssw0rd!", result);
  }

}
