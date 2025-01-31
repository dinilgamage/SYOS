<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="Checkout" />
</jsp:include>

<body class="bg-gray-100 text-gray-900 p-4">

<div class="container mx-auto p-6">
    <h1 class="text-3xl font-bold text-start mb-6">Checkout</h1>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Left Column (Contact, Delivery, Payment) -->
        <div class="md:col-span-2 bg-white p-6 rounded-lg shadow-md">

            <!-- Contact Section -->
            <div class="mb-6">
                <h2 class="text-xl font-semibold mb-4">Contact</h2>
                <input type="email" placeholder="Email" class="w-full p-2 border border-gray-300 rounded">
            </div>

            <!-- Delivery Section -->
            <div class="mb-6">
                <h2 class="text-xl font-semibold mb-4">Delivery</h2>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <input type="text" placeholder="First Name" class="w-full p-2 border border-gray-300 rounded">
                    <input type="text" placeholder="Last Name" class="w-full p-2 border border-gray-300 rounded">
                </div>
                <input type="text" placeholder="Address" class="w-full p-2 border border-gray-300 rounded mt-4">
                <input type="text" placeholder="Apartment, suite, etc. (optional)" class="w-full p-2 border border-gray-300 rounded mt-4">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                    <input type="text" placeholder="City" class="w-full p-2 border border-gray-300 rounded">
                    <input type="text" placeholder="Postal Code (optional)" class="w-full p-2 border border-gray-300 rounded">
                </div>
                <input type="text" placeholder="Phone" class="w-full p-2 border border-gray-300 rounded mt-4">
            </div>

            <!-- Shipping Method -->
            <div class="mb-6">
                <h2 class="text-xl font-semibold mb-4">Shipping Method</h2>
                <label class="flex items-center space-x-3 cursor-pointer">
                    <input type="radio" name="shipping" class="hidden" checked>
                    <div class="w-5 h-5 border border-gray-400 rounded-full flex items-center justify-center">
                        <div class="w-3 h-3 bg-green-500 rounded-full"></div>
                    </div>
                    <span>Standard (3-5 Business Days) - $10.00</span>
                </label>
            </div>

            <!-- Payment Section -->
            <div class="mb-6">
                <h2 class="text-xl font-semibold mb-4">Payment</h2>
                <label class="flex items-center space-x-3 cursor-pointer">
                    <input type="radio" name="payment" value="card" class="hidden" id="cardOption">
                    <div class="w-5 h-5 border border-gray-400 rounded-full flex items-center justify-center">
                        <div class="w-3 h-3 bg-green-500 rounded-full hidden" id="cardCheck"></div>
                    </div>
                    <span>Credit/Debit Card</span>
                    <div class="flex space-x-2 ml-3">
                        <img src="https://img.icons8.com/color/48/visa.png" class="w-8">
                        <img src="https://img.icons8.com/color/48/mastercard.png" class="w-8">
                        <img src="https://img.icons8.com/color/48/amex.png" class="w-8">
                    </div>
                </label>

                <div id="cardDetails" class="hidden mt-4">
                    <input type="text" placeholder="Card Number" class="w-full p-2 border border-gray-300 rounded">
                    <div class="grid grid-cols-2 gap-4 mt-4">
                        <input type="text" placeholder="Expiration Date (MM/YY)" class="w-full p-2 border border-gray-300 rounded">
                        <input type="text" placeholder="Security Code" class="w-full p-2 border border-gray-300 rounded">
                    </div>
                    <input type="text" placeholder="Name on Card" class="w-full p-2 border border-gray-300 rounded mt-4">
                </div>

                <label class="flex items-center space-x-3 cursor-pointer mt-4">
                    <input type="radio" name="payment" value="cod" class="hidden" id="codOption">
                    <div class="w-5 h-5 border border-gray-400 rounded-full flex items-center justify-center">
                        <div class="w-3 h-3 bg-green-500 rounded-full hidden" id="codCheck"></div>
                    </div>
                    <span>Cash on Delivery</span>
                </label>
            </div>

            <button class="w-full bg-green-500 hover:bg-green-600 text-white p-3 rounded-lg">Pay Now</button>
        </div>

        <!-- Right Column: Order Summary -->
        <div class="bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-xl font-semibold mb-4">Order Summary</h2>
            <div id="order-summary" class="space-y-4">
                <!-- Cart items will be dynamically added here -->
            </div>
            <div class="flex justify-between mt-2">
                <span>Shipping</span>
                <span>$10.00</span>
            </div>
            <div class="flex justify-between font-semibold mt-2">
                <span>Total</span>
                <span id="total-price">$0.00</span>
            </div>
        </div>
    </div>
</div>

<script src="js/productModal.js"></script>
<script src="js/cartModal.js"></script>
<script src="js/navbar.js"></script>
<script src="js/checkout.js"></script>

</body>
</html>