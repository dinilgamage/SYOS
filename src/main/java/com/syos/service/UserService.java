package com.syos.service;

import com.syos.dao.UserDao;
import com.syos.exception.UserAlreadyExistsException;
import com.syos.model.User;

public class UserService {

  private UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public void registerUser(User user) {

    if (userDao.getUserByEmail(user.getEmail()) != null) {
      throw new UserAlreadyExistsException("Email '" + user.getEmail() + "' is already registered.");
    }

    userDao.saveUser(user);
  }

  public boolean registerNewUser(User user) {
    if (userDao.getUserByEmail(user.getEmail()) != null) {
      throw new UserAlreadyExistsException("Email '" + user.getEmail() + "' is already registered.");
    }

    return userDao.registerUser(user);
  }

  public boolean loginUser(String email, String password) {

    return userDao.verifyUserCredentials(email, password);
  }

  public User getUserByEmail(String email) {

    return userDao.getUserByEmail(email);
  }
}

