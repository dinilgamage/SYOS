package com.syos.facade;

import com.syos.command.Command;
import com.syos.command.GenerateBillCommand;
import com.syos.command.RestockCommand;
import com.syos.dao.InventoryDao;
import com.syos.enums.DiscountType;
import com.syos.enums.ReportType;
import com.syos.enums.ReportFilterType;
import com.syos.enums.ShelfType;
import com.syos.enums.TransactionType;
import com.syos.exception.UserAlreadyExistsException;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.model.User;
import com.syos.service.DiscountService;
import com.syos.service.ReportService;
import com.syos.service.BillService;
import com.syos.service.InventoryService;
import com.syos.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StoreFacadeImpl implements StoreFacade {

  private final InventoryService inventoryService;
  private final BillService billService;
  private final ReportService reportService;
  private final InventoryDao inventoryDao;
  private final DiscountService discountService;
  private final UserService userService;


  public StoreFacadeImpl(InventoryService inventoryService, BillService billService, ReportService reportService,
    InventoryDao inventoryDao, DiscountService discountService, UserService userService) {
    this.inventoryService = inventoryService;
    this.billService = billService;
    this.reportService = reportService;
    this.inventoryDao = inventoryDao;
    this.discountService = discountService;
    this.userService = userService;
  }

  @Override
  public void registerUser(String name, String email, String password) {

    User user = new User(name, email, password);

    try {
      userService.registerUser(user);
    } catch (UserAlreadyExistsException e) {
      throw e;
    }
  }

  @Override
  public boolean loginUser(String email, String password) {

    return userService.loginUser(email, password);
  }

  @Override
  public Integer getUserId(String email) {

    User user = userService.getUserByEmail(email);
    return user != null ? user.getUserId() : null;
  }

  @Override
  public List<Inventory> getAllItems() {
    return inventoryService.getAllItems();
  }

  @Override
  public Inventory getItemByCode(String itemCode) {
    return inventoryService.getItemByCode(itemCode);
  }

  @Override
  public boolean checkAvailableStock(Inventory inventoryItem, int quantity, TransactionType transactionType) {
    return inventoryService.checkAvailableStock(inventoryItem, quantity, transactionType);
  }

  @Override
  public void generateBill(List<BillItem> billItems, TransactionType transactionType, BigDecimal cashTendered, Integer userId) {
    GenerateBillCommand generateBillCommand;
    if (TransactionType.STORE.equals(transactionType)) {
      // In-store transaction
      generateBillCommand = new GenerateBillCommand(billService, billItems, transactionType, cashTendered);
    } else {
      // Online transaction with user ID
      generateBillCommand = new GenerateBillCommand(billService, billItems, transactionType, userId);
    }
    generateBillCommand.execute();
  }

  @Override
  public void restockItem(String itemCode, ShelfType shelfType) {
    // Create a RestockCommand and execute it
    Command restockCommand = new RestockCommand(inventoryService, itemCode, shelfType);
    restockCommand.execute();
  }

  @Override
  public int calculateTotalStockFromBatches (int itemId) {
    return inventoryService.calculateTotalStockFromBatches(itemId);
  }

  @Override
  public void updateInventoryStock(String itemCode, int quantity, TransactionType shelfType) {
    inventoryService.updateInventoryStock(itemCode, quantity, shelfType);
  }

  @Override
  public void generateReport(ReportType reportType, LocalDate date, ReportFilterType reportFilterType) {
    reportService.generateReport(reportType, date,
      reportFilterType);
  }

  // Overloaded method for reports that don't need date and transactionType
  @Override
  public void generateReport(ReportType reportType) {
    reportService.generateReport(reportType);
  }

  // Overloaded method for reports that only need transactionType
  @Override
  public void generateReport(ReportType reportType, ReportFilterType reportFilterType) {
    reportService.generateReport(reportType,
      reportFilterType);
  }

  @Override
  public BigDecimal applyDiscount(Inventory inventoryItem, BillItem billItem) {
    return discountService.applyDiscount(inventoryItem, billItem);
  }

  @Override
  public void addDiscount(String itemCode, BigDecimal discountValue, DiscountType discountType) {
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
