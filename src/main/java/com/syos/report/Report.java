package com.syos.report;

import com.syos.enums.ReportFilterType;

import java.time.LocalDate;

public abstract class Report {

  // Template method for generating a report
  // Passing 'null' for 'date' or 'transactionType' is acceptable here,
  // as relevant subclasses that don't need these parameters will simply ignore them.
  public final void generate(LocalDate date, ReportFilterType type) {
    prepareHeader();
    collectData(date, type);
    formatReport();
    displayReport(type);
  }

  protected abstract void prepareHeader();
  protected abstract void collectData(LocalDate date, ReportFilterType type);
  protected abstract void formatReport();
  protected abstract void displayReport(ReportFilterType reportFilterType);
}
