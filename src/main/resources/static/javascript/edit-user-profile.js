function toggleChangePasswordForm(showForm) {
    let changePasswordButton = document.getElementById('passwordContainer');
    let changePasswordForm = document.getElementById('changePasswordForm');
    let changePasswordFormInput = document.getElementById('changePasswordFormInput');
    let oldPassword = document.getElementById('oldPassword');
    let newPassword = document.getElementById('newPassword');
    let retypePassword = document.getElementById('retypePassword');

    changePasswordFormInput.value = showForm;

    changePasswordForm.hidden = !showForm;
    changePasswordButton.hidden = showForm;

    if (!showForm) {
        // Clear the fields
        oldPassword.value = '';
        newPassword.value = '';
        retypePassword.value = '';

        // Remove the error class
        oldPassword.classList.remove('error');
        newPassword.classList.remove('error');
        retypePassword.classList.remove('error');

        let errorMessages = document.getElementsByClassName('error-message');

        // Loop through each element and clear its content
        for (let i = 0; i < errorMessages.length; i++) {
            errorMessages[i].innerText = '';
        }
    }
}

window.onload = function() {
    let changePasswordFormInput = document.getElementById('changePasswordFormInput');
    let showForm = changePasswordFormInput.value === 'true';
    toggleChangePasswordForm(showForm);
};
