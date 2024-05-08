document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('form').addEventListener('keydown', function(event) {
        let submitButton = document.getElementById("submitButton");
        let cancelButton = document.getElementById("cancelButton");
        if (event.key === 'Enter' && document.activeElement !== submitButton) {
            if(document.activeElement === cancelButton) {
                cancelButton.click();
            }
            event.preventDefault();
            return false;
        }
    });
});