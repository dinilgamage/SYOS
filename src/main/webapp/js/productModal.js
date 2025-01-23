let quantity = 1;

function openProductModal(itemCode, name, price, discount, desc) {
    document.getElementById('modal-product-image').src = `images/products/` + itemCode + `.jpg`;
    document.getElementById('modal-product-name').textContent = name;
    document.getElementById('modal-product-description').textContent = desc;
    document.getElementById('modal-product-price').textContent = `Price: $` + price;
    quantity = 1;
    document.getElementById('product-quantity').textContent = quantity;

    const modal = document.getElementById('product-modal');
    const modalContent = modal.querySelector('.rounded-lg');

    modal.classList.remove('hidden');

    // Store item details for later use
    modal.dataset.itemCode = itemCode;
    modal.dataset.name = name;
    modal.dataset.price = price;

    // Set visible with transition
    modal.classList.remove('hidden');
    setTimeout(() => {
        modal.classList.remove('opacity-0');
        modalContent.classList.remove('scale-95');
    }, 10); // Small delay to ensure the transition is visible

    // Check if the item is already in the cart
    fetch('isItemInCart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `itemCode=${itemCode}`,
    })
        .then((response) => response.json())
        .then((data) => {
            const addToCartButton = modal.querySelector('button[onclick="addToCart()"]');
            const cartMessage = document.getElementById('already-in-cart-msg');
            const counterSection = document.getElementById('counter-section');

            if (data.isInCart) {
                // Item is already in cart: Show message and disable button
                cartMessage.classList.remove('hidden');


                counterSection.classList.add('hidden');
                addToCartButton.disabled = true;
                addToCartButton.classList.add('bg-gray-400', 'cursor-not-allowed');
                addToCartButton.classList.remove('bg-green-500', 'hover:bg-green-600');
            } else {
                // Item is not in cart: Enable button and remove message
                cartMessage.classList.add('hidden');

                counterSection.classList.remove('hidden');
                addToCartButton.disabled = false;
                addToCartButton.classList.add('bg-green-500', 'hover:bg-green-600');
                addToCartButton.classList.remove('bg-gray-400', 'cursor-not-allowed');
            }
        })
        .catch((error) => {
            console.error('Error checking if item is in cart:', error);
        });
}

function closeProductModal() {
    const modal = document.getElementById('product-modal');
    const modalContent = modal.querySelector('.rounded-lg');

    modal.classList.add('hidden');

    modal.classList.add('opacity-0');
    modalContent.classList.add('scale-95');

    // Wait for the transition to complete before hiding the modal
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 300); // Matches the transition duration
}

function incrementQuantity() {
    quantity++;
    document.getElementById('product-quantity').textContent = quantity;
}

function decrementQuantity() {
    if (quantity > 1) {
        quantity--;
        document.getElementById('product-quantity').textContent = quantity;
    }
}

function addToCart() {
    const modal = document.getElementById('product-modal');
    const itemCode = modal.dataset.itemCode;
    const name = modal.dataset.name;
    const price = modal.dataset.price;

    const cartData = {
        itemCode,
        name,
        price,
        quantity,
    };

    console.log(cartData);

    // Send the cart data to the server using an AJAX call
    fetch('addToCart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(cartData),
    })
        .then((response) => response.json()) // Parse JSON response
        .then((data) => {
            if (data.success) {
                document.getElementById('cart-count').textContent = data.cartSize;
                closeProductModal();
            } else {
                console.error('Failed to add item to cart:', data.message);
                alert(`Failed to add item to cart: ${data.message}`);
            }
        })
        .catch((error) => {
            console.error('Error adding item to cart:', error);
            alert('An error occurred while adding the item to the cart.');
        });
}

