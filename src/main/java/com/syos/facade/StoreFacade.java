package com.syos.facade;

import com.syos.enums.ReportType;
import com.syos.enums.TransactionType;
import com.syos.model.BillItem;
import com.syos.model.Inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StoreFacade {

  void registerUser(String name, String email, String password);
  boolean loginUser(String email, String password);
  Integer getUserId(String email);
  List<Inventory> getAllItems();

  Inventory getItemByCode(String itemCode);
  boolean checkAvailableStock(Inventory inventoryItem, int quantity, String transactionType);
  int calculateTotalStockFromBatches (int itemId);

  void generateBill(List<BillItem> billItems, String transactionType, BigDecimal cashTendered, Integer userId);

  void restockItem(String itemCode, String shelfType);
  void updateInventoryStock(String itemCode, int quantity, String shelfType);

  void generateReport(ReportType reportType);
  void generateReport(ReportType reportType,TransactionType transactionType);
  void generateReport(ReportType reportType, LocalDate date, TransactionType transactionType);

  void addDiscount(String itemCode, BigDecimal discountValue, String strategyType);
  BigDecimal applyDiscount(Inventory inventory, BillItem billItem);
}
