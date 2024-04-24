document.addEventListener('DOMContentLoaded', (event) => {

    document.getElementById('fileInput').addEventListener('change', function(event) {
        var file = event.target.files[0];

        // Get the parent tr element
        var tr = event.target.closest('tr');

        // Find the "Edit" button within the tr element
        var editButton = tr.querySelector('a[href*="edit-plant?plantID="]');

        // Extract the plantId from the href attribute
        var plantId = editButton.href.split('=')[1];

        console.log('plantId: ' + plantId);
        console.log('file: ' + file);

        // Create a new FormData instance
        var formData = new FormData();

        // Append the file and plantId to the FormData instance
        formData.append('file', file);
        formData.append('plantId', plantId);

        fetch('/add-plant-picture', {
            method: 'POST',
            body: formData
        })
    });
});