function openCartModal() {
    console.log('fetchCartItems called'); // Debug log
    fetchCartItems(); // Fetch cart items dynamically

    const modal = document.getElementById('cart-modal');

    // Ensure modal is initially hidden
    modal.classList.remove('hidden');

    // Add visible class with a slight delay to trigger the transition
    setTimeout(() => {
        modal.classList.add('visible');
    }, 10); // Small delay to allow the transition to take effect
}

function closeCartModal() {
    const modal = document.getElementById('cart-modal');

    // Remove the visible class to trigger fade-out and shrink transition
    modal.classList.remove('visible');

    // Wait for the transition to finish before hiding the modal
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 300); // Matches the duration of the transition in CSS
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
            const proceedToCheckoutBtn = document.getElementById('proceed-to-checkout-btn');
            let totalPrice = 0;
            let proceedToCheckout = true;

            if (data.length === 0) {
                cartItemsContainer.innerHTML = '<div class="text-gray-700"> <img src="images/empty-cart.png"' +
                    ' alt="Empty Cart" class="mx-auto cart-image">\n</div>';
                document.getElementById('cart-count').textContent = '0';
                proceedToCheckoutBtn.classList.add('bg-gray-400', 'cursor-not-allowed');
                proceedToCheckoutBtn.classList.remove('bg-green-500', 'hover:bg-green-600');
                proceedToCheckoutBtn.disabled = true; // Disable the button
            } else {
                document.getElementById('cart-count').textContent = data.length;
                proceedToCheckoutBtn.classList.remove('bg-gray-400', 'cursor-not-allowed');
                proceedToCheckoutBtn.classList.add('bg-green-500', 'hover:bg-green-600');
                proceedToCheckoutBtn.disabled = false; // Enable the button
                console.log(data);
                data.forEach((item) => {
                    const cartItem = document.createElement('div');
                    cartItem.className = 'flex justify-between items-center p-4 border rounded';
                    cartItem.dataset.stock = item.stock;
                    cartItem.innerHTML = `
                        <div>
                            <h3 class="text-lg font-semibold">${item.itemName}</h3>
                            <p class="text-gray-800">Price: $${item.price}</p>
                            <p class="text-gray-800">Quantity:
                                <button class="bg-green-200 hover:bg-green-300 px-2 py-1 rounded-lg font-bold" onclick="updateCartItem('${item.itemCode}', ${item.quantity - 1})">-</button>
                                <span class="item-quantity font-semibold mx-1">${item.quantity}</span>
                                <button class="bg-green-200 hover:bg-green-300 px-2 py-1 rounded-lg font-bold" onclick="updateCartItem('${item.itemCode}', ${item.quantity + 1})">+</button>
                            </p>
                            <p class="stock-warning text-red-500 ${item.stock === 0 ? '' : 'hidden'}">
                              ${item.stock === 0 ? 'No stock available' : `Only ${item.stock} left in stock`}
                            </p>
                        </div>
                        <button class="text-red-500" onclick="removeCartItem('${item.itemCode}')">
                            <i style="font-size: 20px" class="fas fa-trash-alt"></i>
                        </button>`;
                    cartItemsContainer.appendChild(cartItem);
                    totalPrice += item.price * item.quantity;

                    if (item.quantity > item.stock) {
                        cartItem.querySelector('.stock-warning').classList.remove('hidden');
                        proceedToCheckout = false;
                    }
                });
            }

            // Update the total price in the cart modal
            document.getElementById('cart-total').textContent = `$${totalPrice.toFixed(2)}`;
            proceedToCheckoutBtn.disabled = !proceedToCheckout;
            if (!proceedToCheckout) {
                proceedToCheckoutBtn.classList.add('bg-gray-400', 'cursor-not-allowed');
                proceedToCheckoutBtn.classList.remove('bg-green-500', 'hover:bg-green-600');
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
    window.location.href = 'checkout.jsp';
}