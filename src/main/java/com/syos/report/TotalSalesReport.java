package com.syos.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.syos.dao.TransactionDao;
import com.syos.enums.ReportFilterType;
import com.syos.enums.TransactionType;
import com.syos.model.Transaction;

public class TotalSalesReport extends Report {

  private TransactionDao transactionDao;
  private BigDecimal totalSales;
  private List<Transaction> transactions;

  // Constructor to inject dependencies (TransactionDao)
  public TotalSalesReport(TransactionDao transactionDao) {
    this.transactionDao = transactionDao;
  }

  @Override
  protected void prepareHeader() {
    System.out.println("=== Total Sales Report ===");
  }

  @Override
  protected void collectData(LocalDate date, ReportFilterType type) {
    System.out.println("Collecting total sales data for " + type + " on " + date);

    // Fetch transactions based on the type and date
    if (type == ReportFilterType.BOTH) {
      transactions = transactionDao.getTransactionsByDate(date); // Combined data for both online and in-store
    } else if (type == ReportFilterType.ONLINE) {
      transactions = transactionDao.getTransactionsByDateAndType(date,
        TransactionType.ONLINE); // Only online transactions
    } else {
      transactions = transactionDao.getTransactionsByDateAndType(date, TransactionType.STORE); // Only in-store transactions
    }

    // Calculate the total sales amount
    totalSales = transactions.stream()
      .map(Transaction::getTotalAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  protected void formatReport() {
    System.out.println("Formatting Total Sales Report...");
  }

  @Override
  protected void displayReport(ReportFilterType reportFilterType) {
    System.out.println("Total Sales: " + totalSales);
    System.out.println("Number of transactions: " + transactions.size());
    transactions.forEach(transaction -> {
      System.out.println("Transaction ID: " + transaction.getTransactionId() +
        ", Type: " + transaction.getTransactionType() +
        ", Amount: " + transaction.getTotalAmount() +
        ", Date: " + transaction.getCreatedAt());
    });
  }
}

