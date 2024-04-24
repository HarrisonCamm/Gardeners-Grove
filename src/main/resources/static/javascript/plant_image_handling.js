document.addEventListener('DOMContentLoaded', (event) => {

    document.getElementById('fileInput').addEventListener('change', function(event) {
        var file = event.target.files[0];

        // Get the parent tr element
        var tr = event.target.closest('tr');

        // Find the "Edit" button within the tr element
        var editButton = tr.querySelector('a[href*="edit-plant?plantID="]');

        // Extract the plantId from the href attribute
        var plantId = editButton.href.split('=')[1];

        // Find the "View" button within the tr element for the garden
        var viewButton = tr.querySelector('a[href*="view-garden?gardenID="]');

        // Extract the gardenId from the href attribute
        var gardenId = viewButton.href.split('=')[1];

        console.log('plantId: ' + plantId);
        console.log('gardenId: ' + gardenId);
        console.log('file: ' + file);

        // Create a new FormData instance
        var formData = new FormData();

        // Append the file, plantId, and gardenId to the FormData instance
        formData.append('file', file);
        formData.append('plantId', plantId);
        formData.append('gardenId', gardenId);

        fetch('/add-plant-picture', {
            method: 'POST',
            body: formData
        })
    });
});