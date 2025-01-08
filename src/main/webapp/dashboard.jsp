<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.syos.model.Inventory" %>

<%
    // Check if the session exists and userEmail is set
    if (session == null || session.getAttribute("userEmail") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Check if inventory items are present
    if (request.getAttribute("inventoryItems") == null) {
        response.sendRedirect("dashboard");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>User Dashboard</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        h2 {
            color: #2c3e50;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .header a {
            text-decoration: none;
            color: #fff;
            background: #e74c3c;
            padding: 8px 12px;
            border-radius: 5px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        table, th, td {
            border: 1px solid #ddd;
        }

        th, td {
            padding: 10px;
            text-align: center;
        }

        th {
            background-color: #3498db;
            color: white;
        }

        tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        .footer {
            margin-top: 30px;
            text-align: center;
            font-size: 0.9em;
            color: #7f8c8d;
        }
    </style>
</head>
<body>

<div class="header">
    <h2>Welcome, <%= session.getAttribute("userEmail") %></h2>
    <a href="logout">Logout</a>
</div>

<h3>Inventory Items</h3>

<table>
    <thead>
    <tr>
        <th>Item Code</th>
        <th>Name</th>
        <th>Price</th>
        <th>Discount</th>
        <th>Store Stock</th>
        <th>Online Stock</th>
        <th>Shelf Capacity</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Inventory> inventoryItems =
                (List<Inventory>) request.getAttribute("inventoryItems");

        if (inventoryItems != null && !inventoryItems.isEmpty()) {
            for (Inventory item : inventoryItems) {
    %>
    <tr>
        <td><%= item.getItemCode() %></td>
        <td><%= item.getName() %></td>
        <td><%= item.getPrice() %></td>
        <td><%= item.getDiscountValue() %> (<%= item.getDiscountType() %>)</td>
        <td><%= item.getStoreStock() %></td>
        <td><%= item.getOnlineStock() %></td>
        <td><%= item.getShelfCapacity() %></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="7">No inventory items available.</td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>

<div class="footer">
    <p>&copy; 2024 SYOS. All rights reserved.</p>
</div>

</body>
</html>
