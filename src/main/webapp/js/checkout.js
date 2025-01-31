document.addEventListener("DOMContentLoaded", function () {
    const cardOption = document.getElementById("cardOption");
    const codOption = document.getElementById("codOption");
    const cardDetails = document.getElementById("cardDetails");
    const cardCheck = document.getElementById("cardCheck");
    const codCheck = document.getElementById("codCheck");

    cardOption.addEventListener("click", function () {
        cardDetails.classList.remove("hidden");
        cardCheck.classList.remove("hidden");
        codCheck.classList.add("hidden");
    });

    codOption.addEventListener("click", function () {
        cardDetails.classList.add("hidden");
        codCheck.classList.remove("hidden");
        cardCheck.classList.add("hidden");
    });

    const shippingOptions = document.querySelectorAll('input[name="shipping"]');
    const orderSummary = document.getElementById('order-summary');
    const totalPriceElement = document.getElementById('total-price');
    let shippingCost = 10;
    let totalPrice = 0;

    function updateTotalPrice() {
        totalPriceElement.textContent = `$${(totalPrice + shippingCost).toFixed(2)}`;
    }

    shippingOptions.forEach(option => {
        option.addEventListener('change', function () {
            shippingCost = this.value === 'same-day' ? 20 : 10;
            updateTotalPrice();
        });
    });

    // Populate order summary with cart items from localStorage
    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
    cartItems.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.className = 'flex justify-between items-center';
        itemElement.innerHTML = `
            <div class="flex items-center">
                <img src="images/products/${item.itemCode}.jpg" alt="${item.itemName}" class="w-16 h-16 object-cover rounded-lg mr-4">
                <div>
                    <h3 class="text-lg font-semibold">${item.itemName}</h3>
                    <p class="text-gray-800">Quantity: ${item.quantity}</p>
                    <p class="text-gray-800">Price: $${item.price}</p>
                </div>
            </div>
            <div class="text-right">
                <p class="text-gray-800">Total: $${(item.price * item.quantity).toFixed(2)}</p>
            </div>
        `;
        orderSummary.appendChild(itemElement);
        totalPrice += item.price * item.quantity;
    });

    updateTotalPrice();
});