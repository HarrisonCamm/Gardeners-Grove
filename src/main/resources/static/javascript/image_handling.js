document.addEventListener('DOMContentLoaded', () => {

    // Select all file inputs
    const fileInputs = document.querySelectorAll('.fileInput');
    console.log(fileInputs.length)

    // Attach the event listener to each file input individually
    fileInputs.forEach(fileInput => {
        fileInput.addEventListener('change', function(event) {
            console.log('File input changed: ' + event.target.files[0].name);

            const file = event.target.files[0];

            validateFile(file);

            let formData = new FormData();

            // Parse the current URL
            const url = new URL(window.location.href);

            // Get the query parameters from the URL
            const params = new URLSearchParams(url.search);

            // Flag to skip the first key
            let isFirstKey = true;

            // Iterate over each parameter and append it to the FormData
            for (const [key, value] of params.entries()) {
                if (isFirstKey) {
                    isFirstKey = false;
                    continue;
                }
                formData.append(key, value);
            }

            formData.append('file', file);
            fetch(url, {
                method: 'PUT',
                body: formData
            })
                .then(response => response.json())
                .then(data => console.log(data))
                .catch(error => console.error(error));
        });
    });
});

function validateFile(file) {
    const allowedExtensions = ['jpg', 'png', 'svg'];
    const fileExtension = file.name.split('.').pop();
    const maxSize = 10 * 1024 * 1024; // 10MB

    if (!allowedExtensions.includes(fileExtension)) {
        alert('Image must be of type png, jpg or svg');
    } else if (file.size > maxSize) {
        alert('Image must be less than 10MB');
    }
}