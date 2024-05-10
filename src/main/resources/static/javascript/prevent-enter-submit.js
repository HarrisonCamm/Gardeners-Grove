document.addEventListener('DOMContentLoaded', function() {
    // Get all forms within the document
    const forms = document.querySelectorAll('form');

    // Iterate over each form
    forms.forEach(function(form) {
        // Add event listener for keydown event
        form.addEventListener('keydown', function(event) {
            let submitButton = form.querySelector('[type="submit"]');
            let cancelButton = form.querySelector('[type="button"]');

            // Check if the pressed key is Enter and the active element is not the submit button
            if (event.key === 'Enter' && document.activeElement !== submitButton) {
                if (document.activeElement === cancelButton) {
                    cancelButton.click();
                }
                event.preventDefault();
                return false;
            }
        });
    });
});
