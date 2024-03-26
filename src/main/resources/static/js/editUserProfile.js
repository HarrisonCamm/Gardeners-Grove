function toggleChangePasswordForm(showForm) {
    var changePasswordButton = document.getElementById('changePasswordButton');
    var changePasswordForm = document.getElementById('changePasswordForm');

    changePasswordForm.value = showForm;

    if (changePasswordForm.value) {
        changePasswordForm.style.display = 'block';
        changePasswordButton.style.display = 'none';
        localStorage.setItem('changePasswordFormStatus', 'block');
    } else {
        changePasswordForm.style.display = 'none';
        changePasswordButton.style.display = 'inline-block'; // Or 'block', depending on your layout
        localStorage.setItem('changePasswordFormStatus', 'none');
    }
}

// When the page loads, check the saved status and set the visibility of the "Change Password" form
window.onload = function() {
    var changePasswordForm = document.getElementById('changePasswordForm');
    var changePasswordButton = document.getElementById('changePasswordButton');
    var savedStatus = localStorage.getItem('changePasswordFormStatus');

    if (savedStatus) {
        changePasswordForm.style.display = savedStatus;
        changePasswordButton.style.display = savedStatus === 'block' ? 'none' : 'inline-block';
    }
};