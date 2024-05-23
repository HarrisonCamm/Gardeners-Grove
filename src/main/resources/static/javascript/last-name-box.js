window.addEventListener('load', function() {
    let checkbox = document.getElementById('noLastName');
    let lastNameLabel = document.getElementById('lastNameLabel');
    if (checkbox.checked) {
        lastNameLabel.textContent = "";
    } else {
        lastNameLabel.textContent = "*";
    }
})

function toggleLastNameField() {
    // Check the checkbox state and enable/disable the last name field accordingly
    let checkbox = document.getElementById('noLastName');
    let lastNameField = document.getElementById('lastName');
    let lastNameLabel = document.getElementById('lastNameLabel');

    lastNameField.value = "";
    lastNameField.disabled = checkbox.checked;
    checkbox.value = checkbox.checked;
    if (checkbox.checked) {
        lastNameLabel.textContent = "";
    } else {
        lastNameLabel.textContent = "*";
    }

}