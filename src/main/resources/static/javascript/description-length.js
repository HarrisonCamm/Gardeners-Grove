function descriptionLength(input) {
    let description = input.value;
    let descriptionLength = description.length;
    let descriptionLengthWarning = document.getElementById('descriptionLengthValidation');
    if (descriptionLength > 512) {
        descriptionLengthWarning.textContent = "Plant description must be less than 512 characters";
    } else {
        descriptionLengthWarning.textContent = "";
    }
}
function countLength(input) {
    let description = input.value;
    let descriptionLength = description.length;
    let descriptionLengthWarning = document.getElementById('descriptionLengthValidation');
    if (descriptionLength > 512) {
        descriptionLengthWarning.textContent = "Plant description must be less than 512 characters";
    } else {
        descriptionLengthWarning.textContent = "";
    }
}