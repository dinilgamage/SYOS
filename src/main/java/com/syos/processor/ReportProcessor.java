package com.syos.processor;

import com.syos.enums.ReportType;
import com.syos.enums.ReportFilterType;
import com.syos.facade.StoreFacade;
import com.syos.util.InputUtils;

import java.time.LocalDate;
import java.util.Scanner;

public class ReportProcessor {

  private final StoreFacade storeFacade;

  public ReportProcessor(StoreFacade storeFacade) {
    this.storeFacade = storeFacade;
  }

  public void generateReports(Scanner scanner) {
    System.out.println("=== Generate Reports ===");
    System.out.println("[1] Total Sales Report");
    System.out.println("[2] Reshelve Report");
    System.out.println("[3] Reorder Report");
    System.out.println("[4] Stock Report");
    System.out.println("[5] Bill Report");

    int reportChoice = InputUtils.getValidatedPositiveInt(scanner, "Please enter a valid report option: ");
    ReportType reportType = null;
    LocalDate date = null;
    ReportFilterType reportFilterType = null;

    switch (reportChoice) {
      case 1:
        reportType = ReportType.TOTAL_SALES;
        date = InputUtils.getValidatedDate(scanner, "Enter Date (yyyy-mm-dd): ");
        String transactionMode = InputUtils.getValidatedStringOption(scanner, "Transaction Mode (online/store/both): ", "online", "store", "both");
        reportFilterType = ReportFilterType.valueOf(transactionMode.toUpperCase());
        break;
      case 2:
        reportType = ReportType.RESHELVE;
        transactionMode = InputUtils.getValidatedStringOption(scanner, "Transaction Mode (online/store/both): ", "online", "store", "both");
        reportFilterType = ReportFilterType.valueOf(transactionMode.toUpperCase());
        break;
      case 3:
        reportType = ReportType.REORDER;
        break;
      case 4:
        reportType = ReportType.STOCK;
        break;
      case 5:
        reportType = ReportType.BILL;
        transactionMode = InputUtils.getValidatedStringOption(scanner, "Transaction Mode (online/store/both): ", "online", "store", "both");
        reportFilterType = ReportFilterType.valueOf(transactionMode.toUpperCase());
        break;
      default:
        System.out.println("Invalid report choice.");
        return;
    }

    storeFacade.generateReport(reportType, date,
      reportFilterType);
  }
}
