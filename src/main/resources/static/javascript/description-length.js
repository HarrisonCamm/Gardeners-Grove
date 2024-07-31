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

document.addEventListener('DOMContentLoaded', () => {
    const plantName = document.getElementById('plantName');
    const nameError = document.getElementById('nameError');

    const maxEnterableLength = Number.parseInt(plantName.getAttribute('maxlength'), 10) ;
    const maxLength = 255;

    plantName.addEventListener('input', (event) => {
        const currentLen = plantName.value.length;

        if (currentLen >= maxEnterableLength) {
            nameError.textContent = 'Plant name is too long, input may be trimmed';
        } else if (currentLen > maxLength) {
            nameError.textContent = 'Plant name must be 255 characters or less';
        } else {
            nameError.textContent = '';
        }
    });
});
