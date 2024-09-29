package com.syos.report;

import com.syos.dao.TransactionDao;
import com.syos.enums.ReportFilterType;
import com.syos.enums.TransactionType;
import com.syos.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillReportTest {

  @Mock
  private TransactionDao mockTransactionDao;

  private BillReport billReport;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    billReport = new BillReport(mockTransactionDao);
  }

  private Transaction createTransaction(int id, TransactionType type, BigDecimal amount) {
    Transaction transaction = mock(Transaction.class);
    when(transaction.getTransactionId()).thenReturn(id);
    when(transaction.getTransactionType()).thenReturn(type);
    when(transaction.getTotalAmount()).thenReturn(amount);
    when(transaction.getCreatedAt()).thenReturn(LocalDateTime.now());
    return transaction;
  }

  /**
   * Test generating a report for both online and in-store transactions (Happy Path).
   */
  @Test
  public void testGenerateReport_BothTransactions_Success() {
    // Arrange
    Transaction onlineTransaction = createTransaction(1, TransactionType.ONLINE, new BigDecimal("100.00"));
    Transaction storeTransaction = createTransaction(2, TransactionType.STORE, new BigDecimal("200.00"));
    when(mockTransactionDao.getAllTransactions()).thenReturn(Arrays.asList(onlineTransaction, storeTransaction));

    // Act
    billReport.collectData(null, ReportFilterType.BOTH);
    billReport.displayReport(ReportFilterType.BOTH);

    // Assert
    List<Transaction> transactions = billReport.getTransactions();
    assertEquals(2, transactions.size());
    verify(mockTransactionDao, times(1)).getAllTransactions();
  }

  /**
   * Test generating a report for online transactions only (Happy Path).
   */
  @Test
  public void testGenerateReport_OnlineTransactions_Success() {
    // Arrange
    Transaction onlineTransaction = createTransaction(1, TransactionType.ONLINE, new BigDecimal("100.00"));
    when(mockTransactionDao.getAllTransactionsByType(TransactionType.ONLINE)).thenReturn(Collections.singletonList(onlineTransaction));

    // Act
    billReport.collectData(null, ReportFilterType.ONLINE);
    billReport.displayReport(ReportFilterType.ONLINE);

    // Assert
    List<Transaction> transactions = billReport.getTransactions();
    assertEquals(1, transactions.size());
    assertEquals(TransactionType.ONLINE, transactions.get(0).getTransactionType());
    verify(mockTransactionDao, times(1)).getAllTransactionsByType(TransactionType.ONLINE);
  }

  /**
   * Test generating a report for store transactions only (Happy Path).
   */
  @Test
  public void testGenerateReport_StoreTransactions_Success() {
    // Arrange
    Transaction storeTransaction = createTransaction(2, TransactionType.STORE, new BigDecimal("200.00"));
    when(mockTransactionDao.getAllTransactionsByType(TransactionType.STORE)).thenReturn(Collections.singletonList(storeTransaction));

    // Act
    billReport.collectData(null, ReportFilterType.STORE);
    billReport.displayReport(ReportFilterType.STORE);

    // Assert
    List<Transaction> transactions = billReport.getTransactions();
    assertEquals(1, transactions.size());
    assertEquals(TransactionType.STORE, transactions.get(0).getTransactionType());
    verify(mockTransactionDao, times(1)).getAllTransactionsByType(TransactionType.STORE);
  }

  /**
   * Test generating a report with no transactions available (Edge Case).
   */
  @Test
  public void testGenerateReport_NoTransactions() {
    // Arrange
    when(mockTransactionDao.getAllTransactions()).thenReturn(Collections.emptyList());

    // Act
    billReport.collectData(null, ReportFilterType.BOTH);
    billReport.displayReport(ReportFilterType.BOTH);

    // Assert
    List<Transaction> transactions = billReport.getTransactions();
    assertTrue(transactions.isEmpty());
    verify(mockTransactionDao, times(1)).getAllTransactions();
  }

  /**
   * Test generating a report when no online transactions are available (Edge Case).
   */
  @Test
  public void testGenerateReport_NoOnlineTransactions() {
    // Arrange
    when(mockTransactionDao.getAllTransactionsByType(TransactionType.ONLINE)).thenReturn(Collections.emptyList());

    // Act
    billReport.collectData(null, ReportFilterType.ONLINE);
    billReport.displayReport(ReportFilterType.ONLINE);

    // Assert
    List<Transaction> transactions = billReport.getTransactions();
    assertTrue(transactions.isEmpty());
    verify(mockTransactionDao, times(1)).getAllTransactionsByType(TransactionType.ONLINE);
  }

  /**
   * Test generating a report when no store transactions are available (Edge Case).
   */
  @Test
  public void testGenerateReport_NoStoreTransactions() {
    // Arrange
    when(mockTransactionDao.getAllTransactionsByType(TransactionType.STORE)).thenReturn(Collections.emptyList());

    // Act
    billReport.collectData(null, ReportFilterType.STORE);
    billReport.displayReport(ReportFilterType.STORE);

    // Assert
    List<Transaction> transactions = billReport.getTransactions();
    assertTrue(transactions.isEmpty());
    verify(mockTransactionDao, times(1)).getAllTransactionsByType(TransactionType.STORE);
  }
}
