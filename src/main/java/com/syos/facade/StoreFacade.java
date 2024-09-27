package com.syos.facade;

import com.syos.enums.ReportType;
import com.syos.enums.TransactionType;
import com.syos.model.BillItem;
import com.syos.model.Inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StoreFacade {

  Inventory getItemByCode(String itemCode);
  boolean checkAvailableStock(Inventory inventoryItem, int quantity, String transactionType);
  int calculateTotalStockFromBatches (int itemId);
  void generateBill(List<BillItem> billItems, String transactionType, BigDecimal cashTendered, Integer userId);
  BigDecimal applyDiscount(Inventory inventory, BillItem billItem);

  void restockItem(String itemCode, String shelfType);

  void updateInventoryStock(String itemCode, int quantity, String shelfType);

  void generateReport(ReportType reportType);
  void generateReport(ReportType reportType,TransactionType transactionType);
  void generateReport(ReportType reportType, LocalDate date, TransactionType transactionType);

  void addDiscount(String itemCode, BigDecimal discountValue, String strategyType);
}
