function formatDate(input) {
    let formattedValue2 = input.value.replace(/[^\d/-]/g, '');
    let formattedValue = formattedValue2.replace(/-/g, '/');

    let matches = formattedValue.match(/\//g);
    if (matches && matches.length > 2) {
        formattedValue = formattedValue.substring(0, formattedValue.lastIndexOf('/'));
    }

    if (formattedValue.length > 2 && formattedValue.charAt(2) !== '/') {
        formattedValue = formattedValue.slice(0, 2) + '/' + formattedValue.slice(2);
    }
    if (formattedValue.length > 5 && formattedValue.charAt(5) !== '/') {
        formattedValue = formattedValue.slice(0, 5) + '/' + formattedValue.slice(5);
    }

    if (formattedValue.length > 10) {
        formattedValue = formattedValue.slice(0, 10);
    }
    input.value = formattedValue;
}