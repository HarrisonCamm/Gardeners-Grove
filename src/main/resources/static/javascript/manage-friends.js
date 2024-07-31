function toggleForm() {
    const form = document.querySelector('.search-container form');
    form.hidden = !form.hidden;
}

function searchAndShowResults() {
    document.getElementById('search-action').value = 'search';
    const form = document.querySelector('.search-container form');
    form.submit();
    const searchFriend = document.getElementById("searchFriend");
    searchFriend.hidden = false;
}

function toggleRemoveFriend(index){
    let removeFriendButton = document.getElementById("removeFriend" + index);
    let confirmRemoveFriendButton = document.getElementById("confirmRemoveFriend" + index);

    removeFriendButton.hidden = !(removeFriendButton.hidden)
    confirmRemoveFriendButton.hidden = !(confirmRemoveFriendButton.hidden)
}

function setActionAndEmail(section, action, email){
    if (action != null) {
        document.getElementById(`${section}-action`).value = action;
    }
    document.getElementById(`${section}-email`).value = email;
}
