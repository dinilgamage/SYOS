package com.syos.dao;

import com.syos.model.User;

public interface UserDao {
  void saveUser(User user);
  boolean registerUser(User user);
  User getUserByEmail(String email);
  boolean verifyUserCredentials(String email, String password);
}

