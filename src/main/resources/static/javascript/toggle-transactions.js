function showDetails(row) {

    // Extract all <td> elements in the clicked row
    var cells = row.getElementsByTagName("td");

    // Access the individual <td> values
    var date = cells[0].innerText;
    var amount = cells[1].innerText;
    var sender = cells[2].innerText;
    var transactionName = cells[3].innerText;
    var transactionType = cells[4].innerText;

    // Hide the table
    document.querySelector('table').style.display = 'none';
    document.getElementById('pagination-wrapper').style.display = 'none';

    // Show the details section
    document.getElementById('transactionDetails').style.display = 'block';

    // Populate the details section with data from the clicked row
    document.getElementById('detailDate').innerText = date;
    document.getElementById('detailAmount').innerText = amount;
    document.getElementById('detailSender').innerText = sender;
    document.getElementById('detailDescription').innerText = transactionName;
    document.getElementById('detailType').innerText = transactionType;
}

function showTable() {
    // Hide the details section
    document.getElementById('transactionDetails').style.display = 'none';

    // Show the table
    document.querySelector('table').style.display = 'table';
    document.getElementById('pagination-wrapper').style.display = 'block';

}
