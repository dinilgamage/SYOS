package com.syos.facade;

import com.syos.enums.DiscountType;
import com.syos.enums.ReportType;
import com.syos.enums.ReportFilterType;
import com.syos.enums.ShelfType;
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
  boolean checkAvailableStock(Inventory inventoryItem, int quantity, TransactionType transactionType);
  int calculateTotalStockFromBatches (int itemId);

  void generateBill(List<BillItem> billItems, TransactionType transactionType, BigDecimal cashTendered, Integer userId);

  void restockItem(String itemCode, ShelfType shelfType);
  void updateInventoryStock(String itemCode, int quantity, TransactionType shelfType);

  void generateReport(ReportType reportType);
  void generateReport(ReportType reportType, ReportFilterType reportFilterType);
  void generateReport(ReportType reportType, LocalDate date, ReportFilterType reportFilterType);

  void addDiscount(String itemCode, BigDecimal discountValue, DiscountType discountType);
  BigDecimal applyDiscount(Inventory inventory, BillItem billItem);
}
