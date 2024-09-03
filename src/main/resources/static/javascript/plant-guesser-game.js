document.addEventListener('DOMContentLoaded', function() {
    const buttons = document.querySelectorAll('.quiz-option-button');

    buttons.forEach(button => {
        button.addEventListener('click', function() {
            buttons.forEach(btn => btn.disabled = true);
            this.classList.add('clicked');
        });
    });
});

function optionClicked() {

}
