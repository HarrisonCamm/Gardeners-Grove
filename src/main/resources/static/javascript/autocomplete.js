function getDeployPath(url) {
    if (url == null)
        url = new URL(window.location.href);
    const deployPath = url.pathname.split('/')[1];
    if (deployPath === 'test' || deployPath === 'prod')
        return '/' + deployPath;
    else
        return '';
}

//Code taken from Geoapify's documentation. See Spike on wiki for details.
function addressAutocomplete(containerElement, callback, options) {

    const MIN_ADDRESS_LENGTH = 5; //Adjust as needed

    const inputContainerElement = document.createElement("div");

    inputContainerElement.setAttribute("class", "input-container");
    containerElement.appendChild(inputContainerElement);

    // create input element
    const inputElement = document.createElement("input");
    inputElement.setAttribute("type", "text");
    inputElement.setAttribute('id', 'addressInput');
    inputElement.setAttribute('class', 'form-control');

    // Append the input element to the container
    inputContainerElement.appendChild(inputElement);

    // Associate the label with the input element

    inputElement.setAttribute("placeholder", options.placeholder);

    /* Focused item in the autocomplete list. This variable is used to navigate with buttons */
    let focusedItemIndex;

    /* Process a user input: */
    inputElement.addEventListener("input", function(e) {
        const currentValue = this.value;

        /* Close any already open dropdown list */
        closeDropDownList();

        // Skip empty or short address strings
        if (!currentValue || currentValue.length < MIN_ADDRESS_LENGTH) {
            return false;
        }

        /* Create a new promise and send geocoding request to the backend */
        const promise = new Promise((resolve, reject) => {
            const deployPath = getDeployPath(null);
            const url = `${deployPath}/getAutocompleteResults?inputString=${currentValue}`; //We create the getAutocompleteResults endpoint Java side
            fetch(url)
                .then(response => {
                    // check if the call was successful
                    if (response.ok) {
                        return response.json(); // Return JSON promise
                    } else {
                        throw new Error('Network response was not ok');
                    }
                })
                .then(data => resolve(data)) // Resolve with data
                .catch(error => reject(error)); // Reject with error
        });

        promise.then((data) => {
            // Parse JSON data
            let json;
            try {
                json = data.results;
            } catch (error) {
                throw new Error('Failed to parse JSON data');
            }

            closeDropDownList(); //close any of results div

            const suggestionsElement = document.getElementById('suggestions');

            /*create a DIV element that will contain the items (values):*/
            const autocompleteItemsElement = document.createElement("div");
            autocompleteItemsElement.setAttribute("class", "autocomplete-items");
            suggestionsElement.appendChild(autocompleteItemsElement);

            if (json.length === 0) { //if there were no results
                const messageElement = document.createElement("div");
                messageElement.textContent = "No matching location found, location-based services may not work";
                autocompleteItemsElement.appendChild(messageElement);
            } else {
                // Iterate through each result
                json.forEach((result, index) => {
                    // Create a DIV element for each result
                    const itemElement = document.createElement("div");
                    // Set formatted address as item value
                    itemElement.innerHTML = result.formatted;
                    autocompleteItemsElement.appendChild(itemElement);

                    // Set the value for the autocomplete text field and notify
                    itemElement.addEventListener("click", function(e) {
                        inputElement.value = result.formatted;
                        callback(result); // Call our callback function to add the address to the rest of the fields
                        inputElement.value = '';
                        // Close the list of autocompleted values
                        closeDropDownList();
                    });
                });
            }
        }).catch(error => {
            console.error(error.message);
        });
    });

    function closeDropDownList() {
        const suggestionsElement = document.getElementById('suggestions');
        const autocompleteItemsElement = suggestionsElement.querySelector(".autocomplete-items");

        if (autocompleteItemsElement) {
            suggestionsElement.removeChild(autocompleteItemsElement);
        }

        focusedItemIndex = -1;
    }

    /* Close the autocomplete dropdown when the document is clicked.
      Skip, when a user clicks on the input field */
    document.addEventListener("click", function(e) {
        if (e.target !== inputElement) {
            closeDropDownList();
        } else if (!containerElement.querySelector(".autocomplete-items")) {
            // open dropdown list again
            var event = document.createEvent('Event');
            event.initEvent('input', true, true);
            inputElement.dispatchEvent(event);
        }
    });
}

// Call the addressAutocomplete function after the DOM has loaded
window.addEventListener('DOMContentLoaded', function () {
    // Get the container element where you want to add the address autocomplete input
    const containerElement = document.getElementById('autocompleteContainer');

    // Define a callback function to handle the selected address data
    function handleAddressSelection(data) {
        const inputArray = getInputFields();
        for (let i = 0; i < inputArray.length; i++) {
            switch (inputArray[i].id) {
                case 'streetAddress':
                    let houseNumber = handleField(data.housenumber);
                    let street = handleField(data.street);
                    inputArray[i].value = houseNumber && street ? houseNumber + ' ' + street : houseNumber + street;
                    break;
                case 'suburb':
                    inputArray[i].value = handleField(data.suburb);
                    break;
                case 'city':
                    inputArray[i].value = handleField(data.county);
                    break;
                case 'postcode':
                    inputArray[i].value = handleField(data.postcode);
                    break;
                case 'country':
                    inputArray[i].value = handleField(data.country);
                    break;
            }
        }
    }


    // Returns an empty string if field is undefined otherwise just return the field
    function handleField(field) {
        return (field) ? field : '';
    }

    function getInputFields() {
        let array = [];
        array.push(document.getElementById('streetAddress'));
        array.push(document.getElementById('suburb'));
        array.push(document.getElementById('city'));
        array.push(document.getElementById('postcode'));
        array.push(document.getElementById('country'));
        return array;
    }

    // Options for the address autocomplete function (e.g., placeholder text)
    const options = {
        placeholder: "Enter an address..." //TODO change to something else
    };

    // Call the addressAutocomplete function with the container element, callback, and options
    addressAutocomplete(containerElement, handleAddressSelection, options);
});