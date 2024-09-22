package com.syos.dao;

import com.syos.model.User;

public interface UserDao {
  void saveUser(User user); // Registers a new user
  User getUserByEmail(String email); // Retrieves user details by email
  boolean verifyUserCredentials(String email, String password); // Validates user login credentials
}

