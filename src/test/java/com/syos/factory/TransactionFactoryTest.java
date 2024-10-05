package com.syos.factory;

import com.syos.enums.TransactionType;
import com.syos.exception.InvalidTransactionTypeException;
import com.syos.factory.TransactionFactory;
import com.syos.model.OnlineTransaction;
import com.syos.model.OverTheCounterTransaction;
import com.syos.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFactoryTest {


  /**
   * Test creating a online transaction (Happy Path).
   */
  @Test
  void testCreateTransaction_OnlineTransaction_Success() {
    // Arrange
    TransactionType type = TransactionType.ONLINE;
    BigDecimal totalAmount = new BigDecimal("100.00");
    Integer userId = 12345;

    // Act
    Transaction transaction = TransactionFactory.createTransaction(type, totalAmount, userId);

    // Assert
    assertNotNull(transaction);
    assertTrue(transaction instanceof OnlineTransaction);
    assertEquals(TransactionType.ONLINE, transaction.getTransactionType());
    assertEquals(totalAmount, transaction.getTotalAmount());
    assertEquals(userId, transaction.getUserId());
  }


  /**
   * Test creating a over the counter transaction (Happy Path).
   */
  @Test
  void testCreateTransaction_OverTheCounterTransaction_Success() {
    // Arrange
    TransactionType type = TransactionType.STORE;
    BigDecimal totalAmount = new BigDecimal("50.00");

    // Act
    Transaction transaction = TransactionFactory.createTransaction(type, totalAmount, null);

    // Assert
    assertNotNull(transaction);
    assertTrue(transaction instanceof OverTheCounterTransaction);
    assertEquals(TransactionType.STORE, transaction.getTransactionType());
    assertEquals(totalAmount, transaction.getTotalAmount());
    assertNull(transaction.getUserId());  // No userId for over-the-counter transactions
  }


  /**
   * Test creating an online transaction with missing user id (edge case).
   */
  @Test
  void testCreateTransaction_OnlineTransaction_MissingUserId() {
    // Arrange
    TransactionType type = TransactionType.ONLINE;
    BigDecimal totalAmount = new BigDecimal("100.00");
    Integer userId = null;

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      TransactionFactory.createTransaction(type, totalAmount, userId);
    });
    assertEquals("User ID is required for online transactions", exception.getMessage());
  }


  /**
   * Test creating a transaction with invalid transaction type (edge case).
   */
  @Test
  void testCreateTransaction_InvalidTransactionType() {
    // Arrange
    TransactionType type = null;  // Invalid transaction type
    BigDecimal totalAmount = new BigDecimal("100.00");
    Integer userId = 12345;

    // Act & Assert
    InvalidTransactionTypeException exception = assertThrows(InvalidTransactionTypeException.class, () -> {
      TransactionFactory.createTransaction(type, totalAmount, userId);
    });
    assertEquals("Invalid Transaction Type", exception.getMessage());
  }
}
