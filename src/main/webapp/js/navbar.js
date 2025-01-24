// Variable to track the state of the dropdown
let isDropdownOpen = false;

function toggleDropdown() {
    const dropdownMenu = document.getElementById('dropdown-menu');
    isDropdownOpen = !isDropdownOpen; // Toggle the dropdown state
    dropdownMenu.classList.toggle('hidden', !isDropdownOpen);
}

function closeDropdown() {
    const dropdownMenu = document.getElementById('dropdown-menu');
    isDropdownOpen = false;
    dropdownMenu.classList.add('hidden');
}

// Close the dropdown if clicked outside
window.addEventListener('click', function (event) {
    const dropdownMenu = document.getElementById('dropdown-menu');
    const profileIcon = document.getElementById('profile-icon');

    // If the click is not on the dropdown menu or profile icon, close the dropdown
    if (
        !dropdownMenu.contains(event.target) &&
        !profileIcon.contains(event.target)
    ) {
        closeDropdown();
    }
});


// Close the dropdown if clicked outside
window.onclick = function(event) {
    if (!event.target.matches('#profile-icon')) {
        var dropdowns = document.getElementsByClassName('dropdown-content');
        for (var i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (!openDropdown.classList.contains('hidden')) {
                openDropdown.classList.add('hidden');
            }
        }
    }
}