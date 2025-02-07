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
    Lock lock = getUserLock(cartItem.getUserId());
    lock.lock();
    try {
      return cartDao.addToCart(cartItem);
    } finally {
      lock.unlock();
    }
  }

  public boolean updateCartItem(int userId, String itemCode, int quantity) {
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.updateCartItem(userId, itemCode, quantity);
    } finally {
      lock.unlock();
    }
  }

  public boolean removeFromCart(int userId, String itemCode) {
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.removeFromCart(userId, itemCode);
    } finally {
      lock.unlock();
    }
  }

  public List<CartItem> getCartItems(int userId) {
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.getCartItems(userId);
    } finally {
      lock.unlock();
    }
  }

  public void clearCart(int userId) {
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      cartDao.clearCart(userId);
    } finally {
      lock.unlock();
    }
  }

  public boolean isItemInCart(int userId, String itemCode) {
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.isItemInCart(userId, itemCode);
    } finally {
      lock.unlock();
    }
  }

  public int getCartSize(int userId) {
    Lock lock = getUserLock(userId);
    lock.lock();
    try {
      return cartDao.getCartSize(userId);
    } finally {
      lock.unlock();
    }
  }
}