const carousel = document.querySelector('.carousel');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');

let currentIndex = 0;
const itemsToShow = 4; // Number of items shown in the carousel at a time
const itemCount = document.querySelectorAll('.carousel-item').length;

// Move the carousel to the left (previous)
prevBtn.addEventListener('click', () => {
    if (currentIndex > 0) {
        currentIndex--;
    } else {
        currentIndex = itemCount - itemsToShow; // Go to the last set
    }
    updateCarousel();
});

// Move the carousel to the right (next)
nextBtn.addEventListener('click', () => {
    if (currentIndex < itemCount - itemsToShow) {
        currentIndex++;
    } else {
        currentIndex = 0; // Go back to the start
    }
    updateCarousel();
});

// Update carousel position
function updateCarousel() {
    console.log("Was clicked");
    const itemWidth = document.querySelector('.carousel-item').offsetWidth;
    carousel.style.transform = `translateX(-${currentIndex * itemWidth}px)`;
}
