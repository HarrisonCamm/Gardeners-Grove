document.addEventListener('DOMContentLoaded', () => {
    // Select all buttons
    const buttons = document.querySelectorAll('button');

    const imageSource = document.querySelectorAll('img');

    let params = new URLSearchParams();

    function setParamsFromUrl() {
        const url = new URL(window.location.href);
        const pathname = url.pathname;

        switch (pathname) {
            case '/view-garden':
                const gardenID = url.searchParams.get('gardenID');
                params.append('view-garden', 'true');
                params.append('gardenID', gardenID);
                break;
            case '/edit-plant':
                const plantID = url.searchParams.get('plantID');
                params.append('edit-plant', 'true');
                params.append('plantID', plantID);
                break;
            //Add more cases as needed
            default:
                console.log('No match!');
        }
    }

    // Set params based on the current URL
    setParamsFromUrl();

    // Attach the event listener to each button individually
    buttons.forEach(button => {
        button.addEventListener('click', function(event) {

            const buttonID = event.target.id;

            const buttonPlantID = buttonID.split('_')[1];

            //Add cases for user profiles as well
            params.set('plantID', buttonPlantID);

            const fetchUrl = '/upload-image?' + params.toString();

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
        if (params.get('view-garden') === 'true') {
            const plantID = image.getAttribute('data-plant-id');
            params.set('plantID', plantID);
            console.log(params.toString())
        }

        fetch('/get-image?' + params.toString())
            .then(response => response.blob())
            .then(blob => {
                image.src = URL.createObjectURL(blob);
            })
            .catch(error => console.error(error));

        image.style.display = "block";
    });
});