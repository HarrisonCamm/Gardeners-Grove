function toggleChangePasswordForm(showForm) {
    let changePasswordButton = document.getElementById('passwordContainer');
    let changePasswordForm = document.getElementById('changePasswordForm');
    let changePasswordFormInput = document.getElementById('changePasswordFormInput');

    changePasswordFormInput.value = showForm;

    changePasswordForm.hidden = !showForm;
    changePasswordButton.hidden = showForm;
}

window.onload = function() {
    let changePasswordFormInput = document.getElementById('changePasswordFormInput');
    let showForm = changePasswordFormInput.value === 'true';
    toggleChangePasswordForm(showForm);
};
