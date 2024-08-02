function toggleWeatherAlert() {
    const form = document.querySelector(".weather-alert");
    form.hidden = !form.hidden;
}

function toggleWaterAlert() {
    const form = document.querySelector(".water-alert");
    form.hidden = !form.hidden;
}

//TODO: Find a way to pass an id to an alertFragment so we can reuse it
// and not have to use two copies of essentially the same fragment

