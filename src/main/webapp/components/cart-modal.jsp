<div id="cart-modal" class="fixed inset-0 bg-black bg-opacity-70 hidden flex justify-center items-center">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-lg sm:max-w-[425px] relative">
        <!-- Modal Header -->
        <div class="flex items-center justify-between p-4 border-b">
            <div class="flex items-center gap-2">
                <i class="fas fa-shopping-bag text-gray-600 text-lg"></i>
                <h3 class="text-xl font-semibold">Your Cart</h3>
            </div>
            <button class="text-gray-500" onclick="closeCartModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>

        <!-- Modal Body -->
        <div id="cart-items" style="max-height: 60vh; overflow-y: auto;"
             class="p-4 space-y-4 ">
            <!-- Placeholder for Empty Cart -->
            <div id="empty-cart-message" class="text-center py-8 hidden">
                <i class="fas fa-shopping-bag text-gray-400 text-6xl"></i>
                <p class="mt-2 text-gray-600">Your cart is empty</p>
                <button class="bg-blue-500 text-white py-2 px-4 rounded mt-4 hover:bg-blue-600" onclick="toggleCartModal()">Start Shopping</button>
            </div>

            <!-- Cart items will be dynamically added here -->
        </div>

        <!-- Modal Footer -->
        <div class="p-4 border-t">
            <div class="flex justify-between items-center">
                <span class="font-semibold">Total:</span>
                <span id="cart-total" class="font-semibold">$0.00</span>
            </div>
            <button class="bg-green-500 text-white w-full py-2 mt-4 rounded hover:bg-green-600">Proceed to Checkout</button>
        </div>
    </div>
</div>
