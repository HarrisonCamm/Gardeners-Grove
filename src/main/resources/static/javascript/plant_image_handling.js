document.addEventListener('DOMContentLoaded', () => {

    // Select all file inputs
    const fileInputs = document.querySelectorAll('.fileInput');
    console.log(fileInputs.length)

    // Attach the event listener to each file input individually
    fileInputs.forEach(fileInput => {
        fileInput.addEventListener('change', function(event) {
            console.log('File input changed: ' + event.target.files[0].name);

            const file = event.target.files[0];

            // Get the parent tr element of the current file input
            const tr = event.target.parentNode.parentNode.parentNode;
            console.log(tr);

            // Find the "Edit" button within the tr element
            const editButton = tr.querySelector('a[href*="edit-plant?plantID="]');

            // Extract the plantId from the href attribute
            const plantId = editButton.href.split('=')[1];

            // Find the "View" button within the tr element for the garden
            const currentUrl = window.location.href;

            // Extract the gardenId from the href attribute
            const gardenId = currentUrl.split('=')[1];

            console.log('Plant ID: ' + plantId);
            console.log('Garden ID: ' + gardenId);

            // Create a new FormData instance
            const formData = new FormData();

            // Append the file, plantId, and gardenId to the FormData instance
            formData.append('file', file);
            formData.append('plantId', plantId);
            formData.append('gardenId', gardenId);

            fetch('/add-plant-picture', {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => console.log(data))
                .catch(error => console.error(error));
        });
    });
});