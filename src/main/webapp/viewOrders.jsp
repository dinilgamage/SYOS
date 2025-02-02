<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="View Orders" />
</jsp:include>

<%
    // Check if the session exists and userEmail is set
    if (session == null || session.getAttribute("userEmail") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<body class="bg-gray-100 text-gray-900">

<jsp:include page="components/navbar.jsp" />

<div class="container mx-auto px-4 pb-6">
    <h1 class="text-3xl font-bold text-start mb-6">Your Orders</h1>
    <div id="orders-container" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <!-- Orders will be dynamically added here -->
    </div>
</div>

<!-- Order Details Modal -->
<div id="order-details-modal" class="fixed inset-0 bg-black bg-opacity-70 hidden flex justify-center items-center">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-lg sm:max-w-[425px] relative">
        <!-- Modal Header -->
        <div class="flex items-center justify-between p-4 border-b">
            <div class="flex items-center gap-2">
                <i class="fas fa-receipt text-gray-600 text-lg"></i>
                <h3 id="order-details-title" class="text-xl font-semibold">Order Details</h3>
            </div>
            <button class="text-gray-500" onclick="closeOrderDetailsModal()">
                <i style="font-size: 25px" class="fas fa-times"></i>
            </button>
        </div>

        <!-- Modal Body -->
        <div id="order-details-content" style="max-height: 60vh; overflow-y: auto;" class="p-6 space-y-4">
            <!-- Order details will be dynamically added here -->
        </div>

        <!-- Modal Footer -->
        <div class="p-4 border-t">
            <div class="flex justify-between items-center mb-4">
                <span class="font-semibold">Total:</span>
                <span id="order-total" class="font-semibold">$0.00</span>
            </div>
            <button onclick="closeOrderDetailsModal()" class="bg-red-500 hover:bg-red-600 text-white w-full py-2 rounded-lg">Close</button>
        </div>
    </div>
</div>

<jsp:include page="components/cart-modal.jsp" />

<script src="js/order.js"></script>
<script src="js/productModal.js"></script>
<script src="js/cartModal.js"></script>
<script src="js/navbar.js"></script>
<script src="js/checkout.js"></script>

</body>