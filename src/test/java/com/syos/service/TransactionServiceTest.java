package com.syos.service;

import com.syos.dao.TransactionDao;
import com.syos.enums.TransactionType;
import com.syos.exception.InvalidTransactionTypeException;
import com.syos.factory.TransactionFactory;
import com.syos.model.OnlineTransaction;
import com.syos.model.OverTheCounterTransaction;
import com.syos.model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

  private TransactionDao mockTransactionDao;
  private TransactionService transactionService;

  @BeforeEach
  public void setUp() {
    mockTransactionDao = mock(TransactionDao.class);
    transactionService = new TransactionService(mockTransactionDao);
  }

  @AfterEach
  public void tearDown() {
    Mockito.clearAllCaches();
  }

  /**
   * Test creating an online transaction (Happy Path).
   */
  @Test
  public void testCreateOnlineTransaction_Success() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    Integer userId = 123;

    // Mock the TransactionFactory to return an OnlineTransaction
    Transaction mockTransaction = new OnlineTransaction(totalAmount, userId);
    mockStatic(TransactionFactory.class);
    when(TransactionFactory.createTransaction(TransactionType.ONLINE, totalAmount, userId))
      .thenReturn(mockTransaction);

    // Act
    Transaction result = transactionService.createTransaction(TransactionType.ONLINE, totalAmount, userId);

    // Assert
    assertNotNull(result);
    assertEquals(TransactionType.ONLINE, result.getTransactionType());
    assertEquals(totalAmount, result.getTotalAmount());
    assertEquals(userId, result.getUserId());
    verify(mockTransactionDao, times(1)).saveTransaction(result);
  }

  /**
   * Test creating a store transaction (Happy Path).
   */
  @Test
  public void testCreateStoreTransaction_Success() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("50.00");

    // Mock the TransactionFactory to return an OverTheCounterTransaction
    Transaction mockTransaction = new OverTheCounterTransaction(totalAmount);
    mockStatic(TransactionFactory.class);
    when(TransactionFactory.createTransaction(TransactionType.STORE, totalAmount, null))
      .thenReturn(mockTransaction);

    // Act
    Transaction result = transactionService.createTransaction(TransactionType.STORE, totalAmount, null);

    // Assert
    assertNotNull(result);
    assertEquals(TransactionType.STORE, result.getTransactionType());
    assertEquals(totalAmount, result.getTotalAmount());
    assertNull(result.getUserId());
    verify(mockTransactionDao, times(1)).saveTransaction(result);
  }

  /**
   * Test creating an online transaction with missing userId (Edge Case).
   */
  @Test
  public void testCreateOnlineTransaction_MissingUserId() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      transactionService.createTransaction(TransactionType.ONLINE, totalAmount, null);
    });
  }

  /**
   * Test creating a transaction with an invalid transaction type (Edge Case).
   */
  @Test
  public void testCreateTransaction_InvalidTransactionType() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");

    // Act & Assert
    assertThrows(InvalidTransactionTypeException.class, () -> {
      transactionService.createTransaction(null, totalAmount, 123);
    });
  }

  /**
   * Test retrieving transactions by date (Happy Path).
   */
  @Test
  public void testGetTransactionsByDate_Success() {
    // Arrange
    LocalDate date = LocalDate.now();
    Transaction mockTransaction1 = new OnlineTransaction(new BigDecimal("100.00"), 123);
    Transaction mockTransaction2 = new OverTheCounterTransaction(new BigDecimal("50.00"));
    List<Transaction> mockTransactions = Arrays.asList(mockTransaction1, mockTransaction2);

    when(mockTransactionDao.getTransactionsByDate(date)).thenReturn(mockTransactions);

    // Act
    List<Transaction> result = transactionService.getTransactionsByDate(date);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(mockTransactionDao, times(1)).getTransactionsByDate(date);
  }

  /**
   * Test retrieving transactions by date when no transactions exist (Edge Case).
   */
  @Test
  public void testGetTransactionsByDate_NoTransactions() {
    // Arrange
    LocalDate date = LocalDate.now();
    when(mockTransactionDao.getTransactionsByDate(date)).thenReturn(Collections.emptyList());

    // Act
    List<Transaction> result = transactionService.getTransactionsByDate(date);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockTransactionDao, times(1)).getTransactionsByDate(date);
  }
}
