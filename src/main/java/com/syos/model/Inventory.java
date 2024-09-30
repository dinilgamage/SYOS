package com.syos.model;

import java.math.BigDecimal;

public class Inventory {
  private int itemId;
  private String itemCode;
  private String name;
  private BigDecimal price;
  private BigDecimal discountValue;
  private String discountType; // "fixed" or "percentage"
  private Integer storeStock;
  private Integer onlineStock;
  private int shelfCapacity;

  // Constructors
  public Inventory(String itemCode, String name, BigDecimal price, Integer storeStock, Integer onlineStock, int shelfCapacity) {
    this.itemCode = itemCode;
    this.name = name;
    this.price = price;
    this.storeStock = storeStock;
    this.onlineStock = onlineStock;
    this.shelfCapacity = shelfCapacity;
    this.discountValue = BigDecimal.ZERO;
    this.discountType = "";
  }

  // Getters and Setters
  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public String getItemCode() {
    return itemCode;
  }

  public void setItemCode(String itemCode) {
    this.itemCode = itemCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getDiscountValue() {
    return discountValue;
  }

  public void setDiscountValue(BigDecimal discountValue) {
    this.discountValue = discountValue;
  }

  public String getDiscountType() {
    return discountType;
  }

  public void setDiscountType(String discountType) {
    this.discountType = discountType;
  }

  public Integer getStoreStock() {
    return storeStock;
  }

  public void setStoreStock(Integer storeStock) {
    this.storeStock = storeStock;
  }

  public Integer getOnlineStock() {
    return onlineStock;
  }

  public void setOnlineStock(Integer onlineStock) {
    this.onlineStock = onlineStock;
  }

  public int getShelfCapacity() {
    return shelfCapacity;
  }

  public void setShelfCapacity(int shelfCapacity) {
    this.shelfCapacity = shelfCapacity;
  }

}

