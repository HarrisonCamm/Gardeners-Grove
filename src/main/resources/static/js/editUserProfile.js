function toggleChangePasswordForm(showForm) {
    let changePasswordButton = document.getElementById('passwordContainer');
    let changePasswordForm = document.getElementById('changePasswordForm');
    let changePasswordFormInput = document.getElementById('changePasswordFormInput');

    changePasswordFormInput.value = showForm;

    changePasswordForm.hidden = !showForm;
    changePasswordButton.hidden = showForm;

}

// // When the page loads, check the saved status and set the visibility of the "Change Password" form
window.onload = function() {
    let changePasswordFormInput = document.getElementById('changePasswordFormInput');
    let showForm = changePasswordFormInput.value === 'true';
    toggleChangePasswordForm(showForm);
};
