function checkTipInput(input) {
    // const maxAmount = 512;
    // todo come up with max amount in case the tip causes the tippee to reach the max blooms allowed in DB
    const userBloomBalance = parseInt(input.dataset.tipInput);
    let tipAmount = parseInt(input.value);

    let tipErrorMessage = document.getElementById('tipAmountErrorMessage');
    let confirmTipButton = document.getElementById('confirmTipButton');

    if (tipAmount <= 0 || isNaN(tipAmount)) {
        tipErrorMessage.textContent = "Tip amount must be a positive number";
        tipErrorMessage.style.color = "red";
        confirmTipButton.disabled = true; // Disable the button
    } else if (tipAmount > userBloomBalance) {
        tipErrorMessage.textContent = "Insufficient Bloom balance";
        confirmTipButton.disabled = true; // Disable the button
        tipErrorMessage.style.color = "red";
    } else {
        tipErrorMessage.textContent = "";
        confirmTipButton.disabled = false // Enable the button
    }
}