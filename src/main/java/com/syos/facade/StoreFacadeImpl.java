package com.syos.facade;

import com.syos.command.Command;
import com.syos.command.GenerateBillCommand;
import com.syos.command.RestockCommand;
import com.syos.dao.InventoryDao;
import com.syos.enums.ReportType;
import com.syos.enums.TransactionType;
import com.syos.factory.DiscountStrategyFactory;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.model.User;
import com.syos.service.DiscountService;
import com.syos.service.ReportService;
import com.syos.service.BillService;
import com.syos.service.InventoryService;
import com.syos.service.UserService;
import com.syos.strategy.DiscountStrategy;

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
    // Create a new User object
    User user = new User(name, email, password);  // Password should ideally be hashed here

    // Delegate the registration logic to the UserService
    boolean isRegistered = userService.registerUser(user);

    if (!isRegistered) {
      throw new IllegalArgumentException("Email already registered.");
    }
  }

  @Override
  public boolean loginUser(String email, String password) {
    // Delegate login logic to the UserService
    return userService.loginUser(email, password);
  }

  @Override
  public Integer getUserId(String email) {
    // You can add this to retrieve the user ID for other purposes (like billing)
    User user = userService.getUserByEmail(email); // Assume this method is available in UserService
    return user != null ? user.getUserId() : null;
  }

  @Override
  public List<Inventory> getAllItems() {
    return inventoryService.getAllItems();  // Call to the InventoryService
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
  public void restockItem(String itemCode, String shelfType) {
    // Create a RestockCommand and execute it
    Command restockCommand = new RestockCommand(inventoryService, itemCode, shelfType);
    restockCommand.execute();
  }

  @Override
  public int calculateTotalStockFromBatches (int itemId) {
    return inventoryService.calculateTotalStockFromBatches(itemId);
  }

  @Override
  public void updateInventoryStock(String itemCode, int quantity, String shelfType) {
    inventoryService.updateInventoryStock(itemCode, quantity, shelfType);
  }

  @Override
  public void generateReport(ReportType reportType, LocalDate date, TransactionType transactionType) {
    reportService.generateReport(reportType, date, transactionType);
  }

  // Overloaded method for reports that don't need date and transactionType
  @Override
  public void generateReport(ReportType reportType) {
    reportService.generateReport(reportType);
  }

  // Overloaded method for reports that only need transactionType
  @Override
  public void generateReport(ReportType reportType, TransactionType transactionType) {
    reportService.generateReport(reportType, transactionType);
  }

  @Override
  public BigDecimal applyDiscount(Inventory inventoryItem, BillItem billItem) {
    return discountService.applyDiscount(inventoryItem, billItem);
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
