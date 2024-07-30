function toggleForm() {
    const form = document.querySelector('.search-container form');
    form.hidden = !form.hidden;
}

function searchAndShowResults() {
    document.getElementById('action').value = 'search';
    const form = document.querySelector('.search-container form');
    form.submit();
    const searchFriend = document.getElementById("searchFriend");
    searchFriend.hidden = false;
}

function toggleRemoveFriend(){
    let removeFriendButton = document.getElementById("removeFriend");
    let confirmRemoveFriendButton = document.getElementById("confirmRemoveFriend");

    removeFriendButton.hidden = !(removeFriendButton.hidden)
    confirmRemoveFriendButton.hidden = !(confirmRemoveFriendButton.hidden)
}

function setActionAndEmail(action, email){
    document.getElementById('friend-request-action').value = action;
    document.getElementById('friend-request-email').value = email;
}
