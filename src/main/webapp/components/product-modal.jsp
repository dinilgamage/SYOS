<div id="product-modal" class="fixed inset-0 bg-black bg-opacity-70 hidden flex justify-center items-center">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-md p-6 relative">
        <img id="modal-product-image" src="#" alt="" class="w-full h-56 object-cover rounded-lg mb-4">
        <div class="flex justify-between items-center">
            <h2 id="modal-product-name" class="text-xl font-semibold"></h2>
            <p id="already-in-cart-msg" class="text-red-500 text-sm hidden">Item already in cart.</p>
        </div>

        <p id="modal-product-description" class="text-gray-600 mt-2"></p>
        <div class="mt-8 flex items-center justify-between">
            <p id="modal-product-price" class="text-primary font-medium text-lg mt-2"></p>

            <div id="counter-section" class="flex items-center gap-2">
                <button class="bg-gray-300 px-3 py-2 rounded" onclick="decrementQuantity()">-</button>
                <span id="product-quantity" class="text-lg">1</span>
                <button class="bg-gray-300 px-3 py-2 rounded" onclick="incrementQuantity()">+</button>
            </div>
        </div>
        <button class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 w-full mt-4"
                onclick="addToCart()">Add to Cart</button>

        <button class="absolute top-0 right-2 text-gray-500" onclick="closeProductModal()">
            <i class="fas fa-times fa-1x"></i>
        </button>
    </div>
</div>
