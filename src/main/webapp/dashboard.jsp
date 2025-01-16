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

<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="Dashboard" />
</jsp:include>
<body class="font-sans bg-gray-100">

<div class="header flex justify-between items-center p-5 bg-white shadow mb-5">
    <h2 class="text-2xl text-gray-800">Welcome, <%= session.getAttribute("userName") %></h2>
    <a href="logout" class="text-white bg-red-500 px-3 py-2 rounded">Logout</a>
</div>

<div class="container mx-auto px-4">
    <h3 class="text-xl mb-3">Inventory Items</h3>
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <%
            List<Inventory> inventoryItems = (List<Inventory>) request.getAttribute("inventoryItems");

            if (inventoryItems != null && !inventoryItems.isEmpty()) {
                for (Inventory item : inventoryItems) {
        %>
        <div class="bg-white p-5 rounded shadow">
            <h4 class="text-lg font-bold mb-2"><%= item.getName() %></h4>
            <p class="text-gray-700 mb-1">Item Code: <%= item.getItemCode() %></p>
            <p class="text-gray-700 mb-1">Price: $<%= item.getPrice() %></p>
            <p class="text-gray-700 mb-1">Discount: <%= item.getDiscountValue() %> (<%= item.getDiscountType() %>)</p>
            <p class="text-gray-700 mb-1">Store Stock: <%= item.getStoreStock() %></p>
            <p class="text-gray-700 mb-1">Online Stock: <%= item.getOnlineStock() %></p>
            <p class="text-gray-700 mb-1">Shelf Capacity: <%= item.getShelfCapacity() %></p>
        </div>
        <%
            }
        } else {
        %>
        <p class="col-span-full text-center text-gray-700">No inventory items available.</p>
        <%
            }
        %>
    </div>
</div>

<div class="footer mt-8 text-center text-sm text-gray-600">
    <p>&copy; 2024 SYOS. All rights reserved.</p>
</div>

</body>
</html>