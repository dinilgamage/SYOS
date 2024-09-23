package com.syos.facade;

import com.syos.command.GenerateBillCommand;
import com.syos.enums.ReportType;
import com.syos.enums.TransactionType;
import com.syos.model.BillItem;
import com.syos.service.ReportService;
import com.syos.service.BillService;
import com.syos.service.InventoryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StoreFacadeImpl implements StoreFacade {

  private final InventoryService inventoryService;
  private final BillService billService;
  private final ReportService reportService;

  public StoreFacadeImpl(InventoryService inventoryService, BillService billService, ReportService reportService) {
    this.inventoryService = inventoryService;
    this.billService = billService;
    this.reportService = reportService;
  }

  @Override
  public void generateBill(List<BillItem> billItems, String transactionType, BigDecimal cashTendered, Integer userId) {
    GenerateBillCommand generateBillCommand;
    if ("over-the-counter".equals(transactionType)) {
      // In-store transaction
      generateBillCommand = new GenerateBillCommand(billService, billItems, transactionType, cashTendered);
    } else {
      // Online transaction with user ID
      generateBillCommand = new GenerateBillCommand(billService, billItems, transactionType, userId);
    }
    generateBillCommand.execute();
  }

  @Override
  public void restockItem(String itemCode, int quantity, String shelfType) {
    inventoryService.restockItem(itemCode, quantity, shelfType);
  }

  @Override
  public void updateInventoryStock(String itemCode, int quantity, String shelfType) {
    inventoryService.updateInventoryStock(itemCode, quantity, shelfType);
  }

  @Override
    public void generateReport(ReportType reportType, LocalDate date, TransactionType transactionMode) {
    reportService.generateReport(reportType, date, transactionMode);
  }
}
