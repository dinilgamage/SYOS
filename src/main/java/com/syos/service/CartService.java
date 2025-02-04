package com.syos.service;

import com.syos.dao.CartDao;
import com.syos.dao.impl.CartDaoImpl;
import com.syos.model.CartItem;

import java.util.List;

public class CartService {

  private final CartDao cartDao;

  public CartService() {
    this.cartDao = new CartDaoImpl();
  }

  public synchronized boolean addToCart(CartItem cartItem) {
    return cartDao.addToCart(cartItem);
  }

  public synchronized boolean updateCartItem(int userId, String itemCode, int quantity) {
    return cartDao.updateCartItem(userId, itemCode, quantity);
  }

  public synchronized boolean removeFromCart(int userId, String itemCode) {
    return cartDao.removeFromCart(userId, itemCode);
  }

  public List<CartItem> getCartItems(int userId) {
    return cartDao.getCartItems(userId);
  }

  public void clearCart(int userId) {
    cartDao.clearCart(userId);
  }

  public boolean isItemInCart(int userId, String itemCode) {
    return cartDao.isItemInCart(userId, itemCode);
  }

  public int getCartSize(int userId) {
    return cartDao.getCartSize(userId);
  }
}
