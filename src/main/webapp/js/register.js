function validateForm() {
    const name = document.forms["registerForm"]["name"];
    const email = document.forms["registerForm"]["email"];
    const password = document.forms["registerForm"]["password"];
    const confirmPassword = document.forms["registerForm"]["confirmPassword"];
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    let isValid = true;

    // Reset previous error states
    name.classList.remove("invalid");
    email.classList.remove("invalid");
    password.classList.remove("invalid");
    confirmPassword.classList.remove("invalid");
    document.getElementById("error-message").innerHTML = "";

    if (name.value === "") {
        name.classList.add("invalid");
        document.getElementById("error-message").innerHTML += "Name must be filled out<br>";
        isValid = false;
    }
    if (!emailPattern.test(email.value)) {
        email.classList.add("invalid");
        document.getElementById("error-message").innerHTML += "Invalid email format<br>";
        isValid = false;
    }
    if (password.value.length < 6) {
        password.classList.add("invalid");
        document.getElementById("error-message").innerHTML += "Password must be at least 6 characters long<br>";
        isValid = false;
    }
    if (password.value !== confirmPassword.value) {
        confirmPassword.classList.add("invalid");
        document.getElementById("error-message").innerHTML += "Passwords do not match<br>";
        isValid = false;
    }
    return isValid;
}