package com.syos.report;

import com.syos.enums.TransactionType;

import java.time.LocalDate;

public abstract class Report {

  // Template method for generating a report
  public final void generate(LocalDate date, TransactionType type) {
    prepareHeader();
    collectData(date, type); // Use the transaction type for data collection
    formatReport();
    displayReport(type);
  }

  // Steps that subclasses will need to implement
  protected abstract void prepareHeader();
  protected abstract void collectData(LocalDate date, TransactionType type); // Pass type to filter data
  protected abstract void formatReport();
  protected abstract void displayReport(TransactionType transactionType);
}
