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

  void generateBill(List<BillItem> billItems, String transactionType, BigDecimal cashTendered, Integer userId);

  void restockItem(String itemCode, int quantity, String shelfType);

  void updateInventoryStock(String itemCode, int quantity, String shelfType);

  void generateReport(ReportType reportType, LocalDate date, TransactionType transactionMode);
  void addDiscount(String itemCode, BigDecimal discountValue, String strategyType);
}
