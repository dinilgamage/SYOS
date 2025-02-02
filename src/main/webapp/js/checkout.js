document.addEventListener("DOMContentLoaded", function () {
    const cardOption = document.getElementById("cardOption");
    const codOption = document.getElementById("codOption");
    const cardDetails = document.getElementById("cardDetails");
    const cardCheck = document.getElementById("cardCheck");
    const codCheck = document.getElementById("codCheck");
    const checkoutForm = document.getElementById("checkoutForm");

    // Ensure COD option is selected by default
    if (codOption.checked) {
        codCheck.classList.remove("hidden");
        cardCheck.classList.add("hidden");
        cardDetails.classList.add("hidden");
    }

    cardOption.addEventListener("click", function () {
        cardDetails.classList.remove("hidden");
        cardCheck.classList.remove("hidden");
        codCheck.classList.add("hidden");
        cardDetails.querySelectorAll('input').forEach(input => {
            input.setAttribute('required', 'required');
        });
    });

    codOption.addEventListener("click", function () {
        cardDetails.classList.add("hidden");
        codCheck.classList.remove("hidden");
        cardCheck.classList.add("hidden");
        cardDetails.querySelectorAll('input').forEach(input => {
            input.removeAttribute('required');
            input.classList.remove('invalid');
        });
    });

    checkoutForm.addEventListener('submit', function (event) {
        const phoneNumber = document.querySelector('input[name="phone"]');
        let valid = true;

        if (!validatePhoneNumber(phoneNumber.value)) {
            phoneNumber.classList.add('invalid');
            valid = false;
        } else {
            phoneNumber.classList.remove('invalid');
        }

        if (cardOption.checked) {
            const cardNumber = document.querySelector('input[name="cardNumber"]');
            const expirationDate = document.querySelector('input[name="expirationDate"]');
            const securityCode = document.querySelector('input[name="securityCode"]');

            if (!validateCardNumber(cardNumber.value)) {
                cardNumber.classList.add('invalid');
                valid = false;
            } else {
                cardNumber.classList.remove('invalid');
            }

            if (!validateExpirationDate(expirationDate.value)) {
                expirationDate.classList.add('invalid');
                valid = false;
            } else {
                expirationDate.classList.remove('invalid');
            }

            if (!validateSecurityCode(securityCode.value)) {
                securityCode.classList.add('invalid');
                valid = false;
            } else {
                securityCode.classList.remove('invalid');
            }
        }

        if (!valid) {
            event.preventDefault();
        }
    });

    function validatePhoneNumber(phoneNumber) {
        const regex = /^[0-9]+$/;
        return regex.test(phoneNumber);
    }

    function validateCardNumber(cardNumber) {
        const regex = /^[0-9]{16}$/;
        return regex.test(cardNumber);
    }

    function validateExpirationDate(expirationDate) {
        const regex = /^(0[1-9]|1[0-2])\/?([0-9]{2})$/;
        if (!regex.test(expirationDate)) {
            return false;
        }
        const [month, year] = expirationDate.split('/');
        const expDate = new Date(`20${year}`, month);
        const currentDate = new Date();
        return expDate > currentDate;
    }

    function validateSecurityCode(securityCode) {
        const regex = /^[0-9]{3,4}$/;
        return regex.test(securityCode);
    }

    const shippingOptions = document.querySelectorAll('input[name="shippingMethod"]');
    const totalPriceElement = document.getElementById('total-price');
    let shippingCost = 10;
    let totalPrice = 0;

    function updateTotalPrice() {
        totalPriceElement.textContent = `$${(totalPrice + shippingCost).toFixed(2)}`;
    }

    function updateShippingVisuals(selectedValue) {
        document.querySelectorAll('.shipping-check').forEach(el => el.classList.add("hidden"));
        if (selectedValue === "same-day") {
            document.getElementById("sameDayCheck").classList.remove("hidden");
        } else {
            document.getElementById("standardCheck").classList.remove("hidden");
        }
        document.getElementById("shipping-cost").textContent = `$${shippingCost.toFixed(2)}`;
    }

    shippingOptions.forEach(option => {
        option.addEventListener('change', function () {
            shippingCost = this.value === 'same-day' ? 20 : 10;
            updateTotalPrice();
            updateShippingVisuals(this.value);
        });
    });

    const orderItems = JSON.parse(localStorage.getItem('cartItems')) || JSON.parse(localStorage.getItem('reorderItems')) || [];
    const orderSummary = document.getElementById('order-summary');

    orderItems.forEach(item => {
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

    // Add this block to read the orderId parameter from the URL and include it in the form submission
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');

    if (orderId) {
        const orderIdInput = document.createElement('input');
        orderIdInput.type = 'hidden';
        orderIdInput.name = 'orderId';
        orderIdInput.value = orderId;
        checkoutForm.appendChild(orderIdInput);
    }
});