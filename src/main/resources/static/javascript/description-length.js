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

// Message Length Checker for direct messages
function messageLength(input) {
    const maxLength = 512;
    let message = input.value;
    let messageLength = message.length;
    let messageLengthCounter = document.getElementById('messageLengthCounter');
    messageLengthCounter.textContent = messageLength + "/" + maxLength;
    if (messageLength > maxLength) {
        messageLengthCounter.style.color = "red";
    } else {
        messageLengthCounter.style.color = "black";
    }
}
