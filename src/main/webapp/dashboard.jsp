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
<body class="font-poppins bg-gray-100">

<!-- Header Section -->
<div class="header flex justify-between items-center p-5 bg-white shadow mb-5">
    <h2 class="text-2xl text-gray-800">Welcome, <%= session.getAttribute("userName") %></h2>
    <a href="logout" class="text-white bg-red-500 px-3 py-2 rounded">Logout</a>
</div>

<!-- Product Cards Section -->
<div class="container mx-auto px-4">
    <h3 class="text-xl mb-3">Inventory Items</h3>
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <%
            List<Inventory> inventoryItems = (List<Inventory>) request.getAttribute("inventoryItems");

            if (inventoryItems != null && !inventoryItems.isEmpty()) {
                for (Inventory item : inventoryItems) {
        %>
        <div class="glass-card bg-white p-4 rounded-xl shadow-lg hover:scale-105 transition-transform cursor-pointer"
             onclick="openProductModal('<%= item.getItemCode() %>', '<%= item.getName() %>',
                     '<%= item.getPrice() %>', '<%= item.getDiscountValue() %>', '<%= item.getDesc() %>')">
            <img src="images/products/<%= item.getItemCode() %>.jpg" alt="<%= item.getName() %>" class="w-full h-48 object-cover rounded-lg mb-4">
            <h4 class="font-semibold text-lg mb-2"><%= item.getName() %></h4>
            <p class="text-primary font-medium text-lg">$<%= item.getPrice() %></p>
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

<!-- Modal Section -->
<div id="product-modal" class="fixed inset-0 bg-black bg-opacity-80 hidden flex justify-center items-center">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-md p-6 relative">
        <img id="modal-product-image" src="#" alt="" class="w-full h-56 object-cover rounded-lg mb-4">
        <h2 id="modal-product-name" class="text-xl font-semibold"></h2>
        <p id="modal-product-description" class="text-gray-600 mt-2"></p>
        <div class="mt-8 flex items-center justify-between">
            <p id="modal-product-price" class="text-primary font-medium text-lg mt-2"></p>

            <div class="flex items-center gap-2">
                <button class="bg-gray-300 px-3 py-2 rounded" onclick="decrementQuantity()">-</button>
                <span id="product-quantity" class="text-lg">1</span>
                <button class="bg-gray-300 px-3 py-2 rounded" onclick="incrementQuantity()">+</button>
            </div>
        </div>
        <button class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 w-full mt-4"
                onclick="addToCart()">Add to
            Cart</button>

        <button class="absolute top-0 right-2 text-gray-500" onclick="closeProductModal()">
            <i class="fas fa-times fa-1x"></i>
        </button>
    </div>
</div>

<div class="footer mt-8 text-center text-sm text-gray-600">
    <p>&copy; 2024 SYOS. All rights reserved.</p>
</div>

<!-- JavaScript for Modal and Cart -->
<script>
    let quantity = 1;

    function openProductModal(itemCode, name, price, discount, desc) {
        document.getElementById('modal-product-image').src = `images/products/` + itemCode + `.jpg`;
        document.getElementById('modal-product-name').textContent = name;
        document.getElementById('modal-product-description').textContent = desc;
        document.getElementById('modal-product-price').textContent = `Price: $` + price;
        quantity = 1;
        document.getElementById('product-quantity').textContent = quantity;

        const modal = document.getElementById('product-modal');
        modal.classList.remove('hidden');
    }

    function closeProductModal() {
        const modal = document.getElementById('product-modal');
        modal.classList.add('hidden');
    }

    function incrementQuantity() {
        quantity++;
        document.getElementById('product-quantity').textContent = quantity;
    }

    function decrementQuantity() {
        if (quantity > 1) {
            quantity--;
            document.getElementById('product-quantity').textContent = quantity;
        }
    }

    function addToCart() {
        alert('Item added to cart with quantity: ' + quantity);
        closeProductModal();
    }
</script>

</body>
</html>
