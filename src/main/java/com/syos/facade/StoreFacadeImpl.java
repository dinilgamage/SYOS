package com.syos.facade;

import com.syos.command.GenerateBillCommand;
import com.syos.dao.InventoryDao;
import com.syos.enums.ReportType;
import com.syos.enums.TransactionType;
import com.syos.factory.DiscountStrategyFactory;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.service.ReportService;
import com.syos.service.BillService;
import com.syos.service.InventoryService;
import com.syos.strategy.DiscountStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StoreFacadeImpl implements StoreFacade {

  private final InventoryService inventoryService;
  private final BillService billService;
  private final ReportService reportService;
  private final InventoryDao inventoryDao;


  public StoreFacadeImpl(InventoryService inventoryService, BillService billService, ReportService reportService,
    InventoryDao inventoryDao) {
    this.inventoryService = inventoryService;
    this.billService = billService;
    this.reportService = reportService;
    this.inventoryDao = inventoryDao;
  }

  @Override
  public Inventory getItemByCode(String itemCode) {
    return inventoryService.getItemByCode(itemCode);
  }

  @Override
  public boolean checkAvailableStock(Inventory inventoryItem, int quantity, String transactionType) {
    return inventoryService.checkAvailableStock(inventoryItem, quantity, transactionType);
  }

  @Override
  public BigDecimal applyDiscount(Inventory inventory, BillItem billItem) {
    // Use the discount strategy to apply any discount to the item
    DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(inventory);
    BigDecimal discountedPrice = discountStrategy.applyDiscount(billItem.getItemPrice());

    // Return the discounted price to be used in the total calculation
    return discountedPrice;
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

  @Override
  public void addDiscount(String itemCode, BigDecimal discountValue, String discountType) {
    // Retrieve the item from the inventory
    Inventory inventory = inventoryDao.getItemByCode(itemCode);

    if (inventory != null) {
      // Set the discount type and value
      inventory.setDiscountType(discountType);
      inventory.setDiscountValue(discountValue);

      // Update the inventory record with the new discount
      inventoryDao.updateInventory(inventory);
    } else {
      throw new IllegalArgumentException("Item not found with code: " + itemCode);
    }
  }


}
