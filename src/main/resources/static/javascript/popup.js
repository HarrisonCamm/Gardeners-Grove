function toggleAlert() {
    console.log("pushed")
    // Get the alert element
    var alertElement = document.querySelector('.alert');

    // Toggle the 'show' class
    alertElement.classList.toggle('show');
}