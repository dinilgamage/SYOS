package com.syos.service;

import com.syos.dao.UserDao;
import com.syos.model.User;

public class UserService {

  private UserDao userDao; // For interacting with user data

  // Constructor to inject dependencies
  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  /**
   * Registers a new user.
   *
   * @param user - The User object containing the user's registration details.
   * @return - Returns true if the user was successfully registered, otherwise false.
   */
  public boolean registerUser(User user) {
    // Check if the email is already registered
    if (userDao.getUserByEmail(user.getEmail()) != null) {
      return false; // Email already exists
    }

    // Save the user and return success
    userDao.saveUser(user);
    return true;
  }

  /**
   * Validates user login credentials.
   *
   * @param email - The email address entered by the user.
   * @param password - The password entered by the user (should be hashed).
   * @return - Returns true if the credentials are valid, otherwise false.
   */
  public boolean loginUser(String email, String password) {
    // Verify user credentials via the UserDao
    return userDao.verifyUserCredentials(email, password);
  }

  /**
   * Retrieves a user by email.
   *
   * @param email - The email address of the user.
   * @return - Returns the User object if found, otherwise null.
   */
  public User getUserByEmail(String email) {
    // Call the DAO method to fetch the user
    return userDao.getUserByEmail(email);
  }
}

