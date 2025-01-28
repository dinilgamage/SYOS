<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Checkout</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100">
<div class="container mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">Checkout</h1>
    <form id="checkoutForm" action="processCheckout" method="post" class="bg-white p-6 rounded shadow-md">
        <div class="mb-4">
            <label for="customerId" class="block text-gray-700">Customer ID:</label>
            <input type="text" id="customerId" name="customerId" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <div class="mb-4">
            <label for="transactionId" class="block text-gray-700">Transaction ID:</label>
            <input type="text" id="transactionId" name="transactionId" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <div class="mb-4">
            <label for="orderDate" class="block text-gray-700">Order Date:</label>
            <input type="date" id="orderDate" name="orderDate" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <div class="mb-4">
            <label for="deliveryDate" class="block text-gray-700">Delivery Date:</label>
            <input type="date" id="deliveryDate" name="deliveryDate" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <div class="mb-4">
            <label for="totalAmount" class="block text-gray-700">Total Amount:</label>
            <input type="number" step="0.01" id="totalAmount" name="totalAmount" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <div class="mb-4">
            <label for="paymentMethod" class="block text-gray-700">Payment Method:</label>
            <input type="text" id="paymentMethod" name="paymentMethod" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <div class="mb-4">
            <label for="orderStatus" class="block text-gray-700">Order Status:</label>
            <input type="text" id="orderStatus" name="orderStatus" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <div class="mb-4">
            <label for="shippingAddress" class="block text-gray-700">Shipping Address:</label>
            <input type="text" id="shippingAddress" name="shippingAddress" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <div class="mb-4">
            <label for="billingAddress" class="block text-gray-700">Billing Address:</label>
            <input type="text" id="billingAddress" name="billingAddress" class="w-full p-2 border border-gray-300 rounded" required>
        </div>
        <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded">Place Order</button>
    </form>
</div>

<script>
    document.getElementById('checkoutForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = new FormData(this);

        fetch('processCheckout', {
            method: 'POST',
            body: new URLSearchParams(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = 'orderConfirmation.jsp';
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => console.error('Error:', error));
    });
</script>
</body>
</html>