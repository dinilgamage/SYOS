<div id="cart-modal" class="fixed inset-0 bg-black bg-opacity-70 hidden flex justify-center items-center">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-lg sm:max-w-[425px] relative">
        <!-- Modal Header -->
        <div class="flex items-center justify-between p-4 border-b">
            <div class="flex items-center gap-2">
                <i class="fas fa-shopping-bag text-gray-600 text-lg"></i>
                <h3 class="text-xl font-semibold">Your Cart</h3>
            </div>
            <button class="text-gray-500" onclick="closeCartModal()">
                <i style="font-size: 25px" class="fas fa-times"></i>
            </button>
        </div>

        <!-- Modal Body -->
        <div id="cart-items" style="max-height: 60vh; overflow-y: auto;"
             class="p-4 space-y-4 ">
            <!-- Placeholder for Empty Cart -->
            <div id="empty-cart-message" class="text-center py-8 hidden">
                <img src="images/empty-cart.png" alt="Empty Cart" class="mx-auto w-24 h-24">
            </div>

            <!-- Cart items will be dynamically added here -->
        </div>

        <!-- Modal Footer -->
        <div id="cart-footer" class="p-4 border-t">
            <div class="flex justify-between items-center">
                <span class="font-semibold">Total:</span>
                <span id="cart-total" class="font-semibold">$0.00</span>
            </div>
            <button id="proceed-to-checkout-btn"
                    class="bg-green-500 text-white w-full py-2 mt-4 rounded-lg hover:bg-green-600"
                    onclick="checkout()">Proceed to
                Checkout</button>
        </div>
    </div>
</div>
