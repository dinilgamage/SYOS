package com.syos.servlet;

import com.syos.exception.UserAlreadyExistsException;
import com.syos.model.User;
import com.syos.service.UserService;
import com.syos.dao.impl.UserDaoImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
  private UserService userService;

  @Override
  public void init() throws ServletException {
    this.userService = new UserService(new UserDaoImpl());
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    try {
      User user = new User(name, email, password);
      int userId = userService.registerNewUser(user);

      if (userId != 0) {
        HttpSession session = request.getSession();
        session.setAttribute("userEmail", email);
        session.setAttribute("userName", user.getName());
        session.setAttribute("userId", userId);
        response.sendRedirect("home.jsp");
      } else {
        request.setAttribute("error", "Registration failed. Email might already exist.");
        request.getRequestDispatcher("register.jsp").forward(request, response);
      }
    } catch (UserAlreadyExistsException e) {
      request.setAttribute("error", e.getMessage());
      request.getRequestDispatcher("register.jsp").forward(request, response);
    } catch (Exception e) {
      throw new ServletException("Error during registration", e);
    }
  }
}
