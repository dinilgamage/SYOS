package com.syos.service;

import com.syos.dao.CartDao;
import com.syos.dao.impl.CartDaoImpl;
import com.syos.model.CartItem;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CartService {

  private final CartDao cartDao;
  private final ConcurrentHashMap<Integer, Lock> userLocks = new ConcurrentHashMap<>();

  public CartService() {
    this.cartDao = new CartDaoImpl();
  }

  private Lock getUserLock(int userId) {
    return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
  }

  public boolean addToCart(CartItem cartItem) {
    validateCartItem(cartItem);
    Lock lock = getUserLock(cartItem.getUserId());
    lock.lock();
    try {
      return cartDao.addToCart(cartItem);
    } finally {
      lock.unlock();
    }
  }

  public boolean updateCartItem(int userId, String itemCode, int quantity) {
    validateUserId(userId);
    validateItemCode(itemCode);
    validateQuantity(quantity);
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.updateCartItem(userId, itemCode, quantity);
    } finally {
      lock.unlock();
    }
  }

  public boolean removeFromCart(int userId, String itemCode) {
    validateUserId(userId);
    validateItemCode(itemCode);
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.removeFromCart(userId, itemCode);
    } finally {
      lock.unlock();
    }
  }

  public List<CartItem> getCartItems(int userId) {
    validateUserId(userId);
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.getCartItems(userId);
    } finally {
      lock.unlock();
    }
  }

  public void clearCart(int userId) {
    validateUserId(userId);
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      cartDao.clearCart(userId);
    } finally {
      lock.unlock();
    }
  }

  public boolean isItemInCart(int userId, String itemCode) {
    validateUserId(userId);
    validateItemCode(itemCode);
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.isItemInCart(userId, itemCode);
    } finally {
      lock.unlock();
    }
  }

  public int getCartSize(int userId) {
    validateUserId(userId);
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.getCartSize(userId);
    } finally {
      lock.unlock();
    }
  }

  private void validateUserId(int userId) {
    if (userId <= 0) {
      throw new IllegalArgumentException("Invalid userId: " + userId);
    }
  }

  private void validateItemCode(String itemCode) {
    if (itemCode == null || itemCode.trim().isEmpty()) {
      throw new IllegalArgumentException("Invalid itemCode: " + itemCode);
    }
  }

  private void validateQuantity(int quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
    }
  }

  private void validateCartItem(CartItem cartItem) {
    validateUserId(cartItem.getUserId());
    validateItemCode(cartItem.getItemCode());
    validateQuantity(cartItem.getQuantity());
    if (cartItem.getItemName() == null || cartItem.getItemName().trim().isEmpty()) {
      throw new IllegalArgumentException("Invalid itemName: " + cartItem.getItemName());
    }
    if (cartItem.getPrice() < 0) {
      throw new IllegalArgumentException("Price cannot be negative: " + cartItem.getPrice());
    }
  }
}