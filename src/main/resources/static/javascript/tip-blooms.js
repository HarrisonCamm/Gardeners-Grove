function checkTipInput(input) {
    // const maxAmount = 512;
    // todo come up with max amount in case the tip causes the tippee to reach the max blooms allowed in DB
    const value = input.value;
    const contains_dot = value.includes(".");

    const userBloomBalance = parseInt(input.dataset.tipInput);
    let possibleTipFloat = parseFloat(input.value);
    let tipAmount = parseInt(input.value);

    let tipErrorMessage = document.getElementById('tipAmountErrorMessage');
    let confirmTipButton = document.getElementById('confirmTipButton');

    // if it's <= 0, empty space, or a decimal
    if (tipAmount <= 0 || isNaN(tipAmount) || possibleTipFloat !== tipAmount || contains_dot) {
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
        input.classList.remove('error'); // Add the error class to the input
    }
}