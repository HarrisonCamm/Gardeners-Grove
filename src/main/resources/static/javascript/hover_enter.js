let hoveredButton = null;

// Track the button that is currently being hovered over
document.addEventListener('mouseover', function(event) {
    if (event.target.tagName === 'BUTTON') {
        hoveredButton = event.target;
    }
});

// Clear the hovered button when the mouse leaves it
document.addEventListener('mouseout', function(event) {
    if (event.target.tagName === 'BUTTON') {
        hoveredButton = null;
    }
});

// Execute a button if the user is hovering over it and presses the enter key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Enter' && hoveredButton) {
        hoveredButton.click();
    }
});