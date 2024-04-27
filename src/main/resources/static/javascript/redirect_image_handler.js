// Mostly generated by GitHub Copilot
document.addEventListener('DOMContentLoaded', () => {

    // Select all buttons
    const buttons = document.querySelectorAll('button');

    const imageSource = document.querySelectorAll('img');

    // Attach the event listener to each button individually
    buttons.forEach(button => {

        button.addEventListener('click', function(event) {
            // Get the button ID
            const buttonId = event.target.id;

            // Remove 'button' from the button ID to get the plant ID
            const plantID = buttonId.replace('button', '');

            const url = new URL(window.location.href);

            // Get the pathname of the URL
            const pathname = url.pathname;

            // Create a new URLSearchParams object
            const params = new URLSearchParams();

            switch (pathname) {
                case '/view-garden':
                    // Extract the plantId from the URL
                    const gardenID = url.searchParams.get('gardenID');
                    console.log('Garden ID: ' + gardenID);
                    params.append('view-garden', 'true');
                    params.append('plantID', plantID);
                    params.append('gardenID', gardenID);
                    break;
                case '/edit-plant':
                    // Extract the gardenId from the URL
                    const paramPlantID = url.searchParams.get('plantID');
                    console.log('Plant ID: ' + plantID);
                    params.append('edit-plant', 'true');
                    params.append('plantID', paramPlantID);
                    break;
                //Add more cases as needed
                default:
                    console.log('No match!');

            }

            const fetchUrl = '/upload-image?' + params.toString();

            console.log('Fetch URL: ' + fetchUrl)

            fetch(fetchUrl, {
                method: 'GET',
            })
                .then(response => response.text())
                .then(data => {
                    document.body.innerHTML = data;
                    history.pushState(null, '', fetchUrl);
                    location.reload();
                })
                .catch(error => console.error(error));
        });
    });

    imageSource.forEach(image => {
        // Retrieve the object URL from localStorage
        const objectUrl = localStorage.getItem('objectUrl');

        if (objectUrl) {
            image.src = objectUrl;
        } else {
            const plantPicture = image.getAttribute('data-picture');
            image.src = '/images/' + plantPicture;
        }
        console.log("Image source: " + image.src)
        image.style.display = "block";
    });
});