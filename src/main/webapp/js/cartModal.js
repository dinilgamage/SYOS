function openCartModal() {
    console.log('fetchCartItems called'); // Debug log
    fetchCartItems(); // Fetch cart items dynamically
    const modal = document.getElementById('cart-modal');
    modal.classList.remove('hidden');
}

function closeCartModal() {
    const modal = document.getElementById('cart-modal');
    modal.classList.add('hidden');
}

function fetchCartItems() {
    console.log("Fetch items called");
    fetch('getCartItems', {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            const cartItemsContainer = document.getElementById('cart-items');
            cartItemsContainer.innerHTML = '';

            if (data.length === 0) {
                cartItemsContainer.innerHTML = '<p class="text-gray-700">Your cart is empty.</p>';
                document.getElementById('cart-count').textContent = '0';
            } else {
                document.getElementById('cart-count').textContent = data.length;
                console.log(data);
                data.forEach((item) => {
                    const cartItem = document.createElement('div');
                    cartItem.className = 'flex justify-between items-center p-4 border rounded';
                    cartItem.innerHTML = `
                        <div>
                            <h3 class="text-lg font-semibold">${item.itemName}</h3>
                            <p class="text-gray-600">Price: $${item.price}</p>
                            <p class="text-gray-600">Quantity: 
                                <button class="bg-gray-300 px-2 py-1 rounded" onclick="updateCartItem('${item.itemCode}', ${item.quantity - 1})">-</button>
                                <span>${item.quantity}</span>
                                <button class="bg-gray-300 px-2 py-1 rounded" onclick="updateCartItem('${item.itemCode}', ${item.quantity + 1})">+</button>
                            </p>
                        </div>
                        <button class="text-red-500" onclick="removeCartItem('${item.itemCode}')">Remove</button>
                    `;
                    cartItemsContainer.appendChild(cartItem);
                });
            }
        })
        .catch((error) => {
            console.error('Error fetching cart items:', error);
        });
}

function updateCartItem(itemCode, quantity) {
    if (quantity < 1) return;

    fetch('updateCartItem', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ itemCode, quantity }),
    })
        .then((response) => response.json())
        .then((data) => {
            if (data.success) {
                fetchCartItems();
            } else {
                alert('Failed to update item quantity.');
            }
        })
        .catch((error) => {
            console.error('Error updating cart item:', error);
        });
}

function removeCartItem(itemCode) {
    fetch('removeCartItem', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ itemCode }),
    })
        .then((response) => response.json())
        .then((data) => {
            if (data.success) {
                fetchCartItems();
            } else {
                alert('Failed to remove item from cart.');
            }
        })
        .catch((error) => {
            console.error('Error removing cart item:', error);
        });
}

function checkout() {
    alert('Checkout functionality is not implemented yet.');
}
