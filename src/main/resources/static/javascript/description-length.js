function descriptionLength(input) {
    const maxLength = 512;
    let description = input.value;
    let descriptionLength = description.length;
    let descriptionLengthCounter = document.getElementById('descriptionLengthCounter');
    descriptionLengthCounter.textContent = descriptionLength + "/" + maxLength;
    if (descriptionLength > maxLength) {
        descriptionLengthCounter.style.color = "red";
    } else {
        descriptionLengthCounter.style.color = "black";
    }
}
