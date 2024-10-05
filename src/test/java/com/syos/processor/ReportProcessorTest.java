package com.syos.processor;

import com.syos.enums.ReportFilterType;
import com.syos.enums.ReportType;
import com.syos.facade.StoreFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReportProcessorTest {

  private StoreFacade mockStoreFacade;
  private Scanner mockScanner;
  private ReportProcessor reportProcessor;

  @BeforeEach
  public void setUp() {
    mockStoreFacade = mock(StoreFacade.class);
    mockScanner = mock(Scanner.class);
    reportProcessor = new ReportProcessor(mockStoreFacade);
  }

  @Test
  public void testGenerateReports_TotalSalesReport() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(1); // User selects total sales report
    when(mockScanner.next()).thenReturn("2023-10-01", "online"); // Date and transaction mode

    // Act
    reportProcessor.generateReports(mockScanner);

    // Assert
    verify(mockStoreFacade).generateReport(eq(ReportType.TOTAL_SALES),
      eq(LocalDate.parse("2023-10-01")),
      eq(ReportFilterType.ONLINE));
  }

  @Test
  public void testGenerateReports_ReshelveReport() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(2); // User selects reshelve report
    when(mockScanner.next()).thenReturn("online"); // Transaction mode

    // Act
    reportProcessor.generateReports(mockScanner);

    // Assert
    verify(mockStoreFacade).generateReport(eq(ReportType.RESHELVE),
      isNull(),
      eq(ReportFilterType.ONLINE));
  }

  @Test
  public void testGenerateReports_ReorderReport() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(3); // User selects reorder report

    // Act
    reportProcessor.generateReports(mockScanner);

    // Assert
    verify(mockStoreFacade).generateReport(eq(ReportType.REORDER),
      isNull(),
      isNull());
  }

  @Test
  public void testGenerateReports_StockReport() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(4); // User selects stock report

    // Act
    reportProcessor.generateReports(mockScanner);

    // Assert
    verify(mockStoreFacade).generateReport(eq(ReportType.STOCK),
      isNull(),
      isNull());
  }

  @Test
  public void testGenerateReports_BillReport() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(5); // User selects bill report
    when(mockScanner.next()).thenReturn("store"); // Transaction mode

    // Act
    reportProcessor.generateReports(mockScanner);

    // Assert
    verify(mockStoreFacade).generateReport(eq(ReportType.BILL),
      isNull(),
      eq(ReportFilterType.STORE));
  }

  @Test
  public void testGenerateReports_InvalidReportChoice() {
    // Arrange
    when(mockScanner.nextInt()).thenReturn(99); // Invalid report choice

    // Act
    reportProcessor.generateReports(mockScanner);

    // Assert
    verify(mockStoreFacade, never()).generateReport(any(), any(), any());
  }

}
