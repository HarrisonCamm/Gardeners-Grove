function getDeploymentContextPath(url) {
    if (url == null)
        url = new URL(window.location.href);
    const deployPath = url.pathname.split('/')[1];
    if (deployPath === 'test' || deployPath === 'prod')
        return '/' + deployPath;
    else
        return '';
}

let stompClient = null;
let to;
let from;

function showMessage(message) {

    // Getting the email of the chat to append the message to that chat area
    const chatEmail = from === message.sender ? message.recipient : message.sender;

    const chatArea = document.getElementById('chatArea-' + chatEmail);
    const messageElement = document.createElement('div');
    messageElement.classList.add('d-flex', 'mb-3');

    let noMessagesText = document.getElementById("no-messages-text");
    if (noMessagesText) {
        noMessagesText.setAttribute('style', 'display: none !important;');
    }

    // Show the message left or right using BootStrap classes
    const isSender = message.sender === from;
    const messageClass = isSender ? 'align-self-end text-end ms-auto' : 'align-self-start text-start me-auto';

    // Determine the background color based on the message status
    let backgroundColor;
    switch (message.status) {
        case 'blocked':
            backgroundColor = 'bg-danger error'; // Red for blocked messages
            message.content = 'Message contains inappropriate language language or is too long';
            break;
        case 'evaluating':
            backgroundColor = 'bg-warning'; // Yellow for pending messages
            message.content = 'Message is being evaluated by the system';
            break;
        default:
            backgroundColor = isSender ? 'background' : 'bg-light'; // Green if sender or gray if received
            break;
    }

    // EACH MESSAGE HTML
    messageElement.innerHTML = `
        <div class="${messageClass} ${backgroundColor} p-3 rounded w-20 text-break">
            ${message.status === 'sent' ? `<strong>${message.sender}</strong>: ` : ''}${message.content} <br>
            <small class="text-muted">${new Date(message.timestamp).toLocaleTimeString()}</small>
        </div>`;

    // Append the message to the chat area
    chatArea.appendChild(messageElement);
    chatArea.scrollTop = chatArea.scrollHeight;

    // Update the last message sent
    const lastMessageElement = document.getElementById('friend-last-message-' + chatEmail);
    let lastMessageContent = message.content;
    if (lastMessageContent.length > 10) {
        lastMessageContent = lastMessageContent.substring(0, 10) + '...';
    }
    lastMessageElement.textContent = lastMessageContent;
}


function connect() {
    // Determine the correct WebSocket URL based on the current environment
    let socketUrl;
    const url = new URL(window.location.href);
    const deployPath = getDeploymentContextPath(url);
    if (deployPath != null && deployPath.length > 0) {
        socketUrl = 'https://csse-seng302-team600.canterbury.ac.nz' + deployPath + '/ws';
    } else {
        socketUrl = url.origin + '/ws';
    }

    // Create a WebSocket connection
    const socket = new WebSocket(socketUrl);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // Subscribe to the user's queue to receive messages
        stompClient.subscribe('/user/queue/reply', function(messageOutput) {
            showMessage(JSON.parse(messageOutput.body));
        });
    });
}

function sendMessage(userId) {
    const messageInput = document.getElementById('message-input-' + userId);
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            sender: from,
            recipient: to.email,
            content: messageContent,
            timestamp: new Date().toISOString(),
            status: "pending"
        };

        let messageProcessed = false;

        fetch('/message/status?content=' + encodeURIComponent(chatMessage.content), {
            method: 'GET',
        })
            .then(response => response.text())
            .then(status => {
                if (!messageProcessed) {
                    chatMessage.status = status;
                    messageProcessed = true;

                    if (chatMessage.status === "sent") {
                        stompClient.send("/app/chat.send/" + to.email, {}, JSON.stringify(chatMessage));
                    }

                    messageInput.value = '';
                    let noMessagesText = document.getElementById("no-messages-text");
                    if (noMessagesText) {
                        noMessagesText.setAttribute('style', 'display: none !important;');
                    }
                    showMessage(chatMessage, userId);
                }
            });
    }

    messageLength(messageInput);
}

function showChatUI(userId, firstName, lastName, email) {
    to = { id: userId, email: email };

    const welcome = document.getElementById('welcomeMessage');
    welcome.setAttribute('style', 'display: none !important;');

    document.querySelectorAll('[id^="chatUI-"]').forEach(chatUI =>
        chatUI.setAttribute('style', 'display: none !important;')
    );

    const currChat = document.getElementById('chatUI-' + userId);
    currChat.setAttribute('style', 'display: flex !important;');

    lastName = lastName ? lastName : '';
    document.getElementById('friendName-' + userId).innerText = firstName + ' ' + lastName;

    const deployPath = getDeploymentContextPath(null);
    document.getElementById('friendImage-' + userId).src = deployPath + '/get-image?view-user-profile=true&userID=' + userId;

    const messageInput = document.getElementById('message-input-' + userId);
    messageInput.focus();
    messageLength(messageInput);

    fetch(deployPath + '/chat/' + to.email)
        .then(response => response.json())
        .then(messages => {
            const chatArea = document.getElementById('chatArea-' + to.email);
            chatArea.innerHTML = '';
            if (messages.length > 0) {
                let noMessagesText = document.getElementById("no-messages-text");
                if (noMessagesText) {
                    noMessagesText.setAttribute('style', 'display: none !important;');
                }
                messages.forEach(message => showMessage(message));
            } else {
                chatArea.innerHTML = '<p id="no-messages-text" class="text-muted">No messages yet. Start the conversation!</p>';
            }
        });
}

document.addEventListener("DOMContentLoaded", function() {
    from = document.getElementById('chat-container').dataset.from;

    document.querySelectorAll('.chat-link').forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            const userId = this.dataset.userId;
            const firstName = this.dataset.firstName;
            const lastName = this.dataset.lastName;
            const email = this.dataset.email;
            showChatUI(userId, firstName, lastName, email);
        });
    });

    document.querySelectorAll('[id^="message-input-"]').forEach(input => {
        input.addEventListener('keydown', function(event) {
            if (event.keyCode === 13) {
                event.preventDefault();
                const userId = this.dataset.userId;
                sendMessage(userId);
            }
        });
    });

    connect(); // Connect to the WebSocket
});
