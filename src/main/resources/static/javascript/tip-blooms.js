function checkTipInput(input) {
    const value = input.value.trim();
    const isWholeNumber = /^\d+$/.test(value); // Check if the input is a whole number

    const userBloomBalance = parseInt(input.dataset.tipInput);
    let tipAmount = parseInt(value);

    let tipErrorMessage = document.getElementById('tipAmountErrorMessage');
    let confirmTipButton = document.getElementById('confirmTipButton');

    // if it's <= 0, empty space, or a decimal
    if (value.trim() === '') {
        confirmTipButton.disabled = true; // Disable the button
        confirmTipButton.style.backgroundColor = "gray"; // Set background color to gray
        confirmTipButton.style.color = "white"; // Set text color to white
        tipErrorMessage.textContent = "";
        input.classList.remove('error'); // Remove the error class from the input
    }
    // if it's empty, not a whole number, or <= 0
    else if (!isWholeNumber || tipAmount <= 0) {
        tipErrorMessage.textContent = "Tip amount must be a positive integer";
        tipErrorMessage.style.color = "red";
        confirmTipButton.disabled = true; // Disable the button
        confirmTipButton.style.backgroundColor = "gray"; // Set background color to gray
        confirmTipButton.style.color = "white"; // Set text color to white
        input.classList.add('error'); // Add the error class to the input
    } else if (tipAmount > userBloomBalance) {
        tipErrorMessage.textContent = "Insufficient Bloom balance";
        confirmTipButton.disabled = true; // Disable the button
        confirmTipButton.style.backgroundColor = "gray"; // Set background color to gray
        confirmTipButton.style.color = "white"; // Set text color to white
        tipErrorMessage.style.color = "red";
        input.classList.add('error'); // Add the error class to the input
    } else {
        tipErrorMessage.textContent = "";
        confirmTipButton.disabled = false // Enable the button
        confirmTipButton.style.backgroundColor = "green"; // Set background color to green
        confirmTipButton.style.color = "white";
        input.classList.remove('error'); // Remove the error class from the input
    }
}
function toggleClaimTips(){
    let claimTipsButton = document.getElementById("claimTipsButton");
    let confirmClaimTipsDiv = document.getElementById("confirmClaimTipsDiv");


    claimTipsButton.hidden = !(claimTipsButton.hidden)
    confirmClaimTipsDiv.hidden = !(confirmClaimTipsDiv.hidden)
}