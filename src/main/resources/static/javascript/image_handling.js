document.addEventListener('DOMContentLoaded', () => {

    // Select the button
    const button = document.querySelector('button');

    const fileInput = document.querySelector('.fileInput');

    button.addEventListener('click', function(event) {
        // Select all file inputs
        console.log("Button clicked")
        const fileInputs = document.querySelectorAll('.fileInput');
        console.log(fileInputs.length)

        // Process each file input individually
        fileInputs.forEach(fileInput => {

            if (fileInput.files.length === 0) {
                alert('Please select a file')
                return;
            }

            const file = fileInput.files[0];

            validateFile(file);

            let formData = new FormData();

            // Parse the current URL
            const url = new URL(window.location.href);

            // Get the query parameters from the URL
            const params = new URLSearchParams(url.search);

            let fetchURL = null

            // Flag to skip the first key
            let isFirstKey = true;

            // Iterate over each parameter and append it to the FormData
            for (const [key, value] of params.entries()) {
                if (isFirstKey) {
                    fetchURL = key
                    isFirstKey = false;
                    continue;
                }
                formData.append(key, value);
            }

            console.log('Fetch URL: ' + fetchURL)

            formData.append('file', file);
            fetch(fetchURL, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.redirected) {
                        window.location.href = response.url;
                    }
                })
                .catch(error => console.error(error));
        });
    });

    // Add an event listener to the file input
    fileInput.addEventListener('change', function(event) {
        console.log("File input changed")
        const imageSource = document.getElementById('imageSource');
        imageSource.src = 'images/' + event.target.files[0].name;
        console.log(imageSource.class)
        imageSource.style.display = "block";
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