package com.syos.report;

import com.syos.dao.TransactionDao;
import com.syos.enums.ReportFilterType;
import com.syos.enums.TransactionType;
import com.syos.model.Transaction;
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

public class TotalSalesReportTest {

  private TransactionDao mockTransactionDao;
  private TotalSalesReport totalSalesReport;

  @BeforeEach
  public void setUp() {
    mockTransactionDao = mock(TransactionDao.class);
    totalSalesReport = new TotalSalesReport(mockTransactionDao);
  }

  /**
   * Helper method to create a sample Transaction.
   */
  private Transaction createTransaction(int id, TransactionType type, BigDecimal amount) {
    Transaction transaction = mock(Transaction.class);
    when(transaction.getTransactionId()).thenReturn(id);
    when(transaction.getTransactionType()).thenReturn(type);
    when(transaction.getTotalAmount()).thenReturn(amount);
    when(transaction.getCreatedAt()).thenReturn(LocalDate.now().atStartOfDay());
    return transaction;
  }

  /**
   * Test generating a total sales report for both online and in-store transactions (Happy Path).
   */
  @Test
  public void testGenerateReport_BothTransactions_Success() {
    // Arrange
    LocalDate date = LocalDate.now();
    Transaction transaction1 = createTransaction(1, TransactionType.ONLINE, new BigDecimal("100.00"));
    Transaction transaction2 = createTransaction(2, TransactionType.STORE, new BigDecimal("150.00"));

    when(mockTransactionDao.getTransactionsByDate(date)).thenReturn(Arrays.asList(transaction1, transaction2));

    // Act
    totalSalesReport.collectData(date, ReportFilterType.BOTH);
    totalSalesReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertEquals(new BigDecimal("250.00"), totalSalesReport.getTotalSales());
    assertEquals(2, totalSalesReport.getTransactions().size());
    verify(mockTransactionDao, times(1)).getTransactionsByDate(date);
  }

  /**
   * Test generating a total sales report for only online transactions (Happy Path).
   */
  @Test
  public void testGenerateReport_OnlineTransactions_Success() {
    // Arrange
    LocalDate date = LocalDate.now();
    Transaction transaction1 = createTransaction(1, TransactionType.ONLINE, new BigDecimal("100.00"));

    when(mockTransactionDao.getTransactionsByDateAndType(date, TransactionType.ONLINE)).thenReturn(Collections.singletonList(transaction1));

    // Act
    totalSalesReport.collectData(date, ReportFilterType.ONLINE);
    totalSalesReport.displayReport(ReportFilterType.ONLINE);

    // Assert
    assertEquals(new BigDecimal("100.00"), totalSalesReport.getTotalSales());
    assertEquals(1, totalSalesReport.getTransactions().size());
    verify(mockTransactionDao, times(1)).getTransactionsByDateAndType(date, TransactionType.ONLINE);
  }

  /**
   * Test generating a total sales report for only in-store transactions (Happy Path).
   */
  @Test
  public void testGenerateReport_StoreTransactions_Success() {
    // Arrange
    LocalDate date = LocalDate.now();
    Transaction transaction1 = createTransaction(1, TransactionType.STORE, new BigDecimal("150.00"));

    when(mockTransactionDao.getTransactionsByDateAndType(date, TransactionType.STORE)).thenReturn(Collections.singletonList(transaction1));

    // Act
    totalSalesReport.collectData(date, ReportFilterType.STORE);
    totalSalesReport.displayReport(ReportFilterType.STORE);

    // Assert
    assertEquals(new BigDecimal("150.00"), totalSalesReport.getTotalSales());
    assertEquals(1, totalSalesReport.getTransactions().size());
    verify(mockTransactionDao, times(1)).getTransactionsByDateAndType(date, TransactionType.STORE);
  }

  /**
   * Test generating a report when no transactions exist for the given date (Edge Case).
   */
  @Test
  public void testGenerateReport_NoTransactions() {
    // Arrange
    LocalDate date = LocalDate.now();
    when(mockTransactionDao.getTransactionsByDate(date)).thenReturn(Collections.emptyList());

    // Act
    totalSalesReport.collectData(date, ReportFilterType.BOTH);
    totalSalesReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertEquals(BigDecimal.ZERO, totalSalesReport.getTotalSales());
    assertEquals(0, totalSalesReport.getTransactions().size());
    verify(mockTransactionDao, times(1)).getTransactionsByDate(date);
  }

}
