function toggleChangePasswordForm(showForm) {
    var changePasswordButton = document.getElementById('changePasswordButton');
    var changePasswordForm = document.getElementById('changePasswordForm');

    if (showForm) {
        changePasswordForm.style.display = 'block';
        changePasswordButton.style.display = 'none';
    } else {
        changePasswordForm.style.display = 'none';
        changePasswordButton.style.display = 'inline-block'; // Or 'block', depending on your layout
    }
}