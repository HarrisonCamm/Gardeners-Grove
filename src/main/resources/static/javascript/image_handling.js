document.getElementById('fileInput').addEventListener('change', function(event) {
    var file = event.target.files[0];
    var formData = new FormData();
    formData.append('file', file);

    // Get the parent tr element
    var tr = event.target.closest('tr');

    // Find the "Edit" button within the tr element
    var editButton = tr.querySelector('a[href*="edit-plant?plantID="]');

    // Extract the plantId from the href attribute
    var plantId = editButton.href.split('=')[1];

    formData.append('plantId', plantId);
    console.log('plantId: ' + plantId);
    console.log('file: ' + file);

    fetch('/add-plant-picture', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => console.log(data))
        .catch(error => console.error(error));
});