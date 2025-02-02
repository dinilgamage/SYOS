document.addEventListener("DOMContentLoaded", function () {
    // Check for order success query parameter and display notification
    const urlParams = new URLSearchParams(window.location.search);
    const orderSuccess = urlParams.get('orderSuccess');

    if (orderSuccess) {
        const notification = document.getElementById('notification');
        console.log("we made it here")
        notification.classList.remove('hidden');

        // Hide the notification after 5 seconds
        setTimeout(() => {
            notification.classList.add('hidden');
        }, 5000);

        const newUrl = window.location.origin + window.location.pathname;
        window.history.replaceState({}, document.title, newUrl);
    }

    const inventoryContainer = document.getElementById('inventory-container');
    const cartSizeElement = document.getElementById('cart-count');

    // Fetch and update cart size
    fetch('getCartSize')
        .then(response => response.json())
        .then(data => {
            cartSizeElement.textContent = data.cartSize || 0;
        })
        .catch(error => {
            console.error('Error fetching cart size:', error);
        });

    // Fetch and display inventory items
    fetch('home')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const { inventoryItems } = data;

            if (inventoryItems.length === 0) {
                inventoryContainer.innerHTML = '<p class="col-span-full text-center text-gray-700">No inventory items available.</p>';
                return;
            }

            inventoryItems.forEach(item => {
                const isOutOfStock = item.onlineStock <= 0;
                const itemCard = document.createElement('div');
                itemCard.className = `glass-card bg-white p-4 rounded-xl shadow-lg ${isOutOfStock ? "" : "hover:scale-105 transition-transform cursor-pointer"}`;
                itemCard.onclick = isOutOfStock ? null : () => openProductModal(item.itemCode, item.name, item.price, item.discountValue, item.desc);

                itemCard.innerHTML = `
                    <img src="images/products/${item.itemCode}.jpg" alt="${item.name}" class="w-full h-48 object-cover rounded-lg mb-4">
                    <h4 class="font-semibold text-lg mb-2">${item.name}</h4>
                    <div class="flex justify-between items-center">
                        <p class="text-primary font-medium text-lg">$${item.price}</p>
                        ${isOutOfStock ? '<p class="text-red-500 font-medium">Out of Stock</p>' : ''}
                    </div>
                `;
                inventoryContainer.appendChild(itemCard);
            });
        })
        .catch(error => {
            console.error('Error fetching inventory items:', error);
            inventoryContainer.innerHTML = `<p class="col-span-full text-center text-gray-700">Failed to load inventory items. Error: ${error.message}</p>`;
        });
});