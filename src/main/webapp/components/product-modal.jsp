<div id="product-modal"
     class="fixed inset-0 bg-black bg-opacity-70 hidden flex justify-center items-center transition-opacity duration-300 ease-in-out opacity-0">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-md p-6 relative transform scale-95 transition-transform duration-300 ease-in-out">
        <img id="modal-product-image" src="#" alt="" class="w-full h-56 object-cover rounded-lg mb-4">
        <div class="flex justify-between items-center">
            <h2 id="modal-product-name" class="text-xl font-semibold"></h2>
            <p id="already-in-cart-msg" class="text-red-500 text-sm hidden">Item already in cart.</p>
        </div>

        <p id="modal-product-description" class="text-gray-600 mt-2"></p>
        <div class="mt-8 flex items-center justify-between">
            <p id="modal-product-price" class="text-primary font-medium text-lg mt-2"></p>

            <div id="counter-section" class="flex items-center gap-2">
                <button
                        class="bg-green-200 hover:bg-green-300 w-10 h-10 rounded-lg font-bold flex items-center justify-center"
                        onclick="decrementQuantity()">-</button>
                <span id="product-quantity" class="text-lg font-semibold">1</span>
                <button
                        class="bg-green-200 hover:bg-green-300 w-10 h-10 rounded-lg font-bold flex items-center justify-center"
                        onclick="incrementQuantity()">+</button>
            </div>
        </div>
        <button class="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 w-full mt-4"
                onclick="addToCart()">Add to Cart</button>

        <button class="absolute top-1 right-2 text-gray-500" onclick="closeProductModal()">
            <i style="font-size: 20px" class="fas fa-times fa-1x"></i>
        </button>
    </div>
</div>
