function getDeploymentContextPath(url) {
    if (url == null)
        url = new URL(window.location.href);
    const deployPath = url.pathname.split('/')[1];
    if (deployPath === 'test' || deployPath === 'prod')
        return '/' + deployPath;
    else
        return '';

}

document.addEventListener("DOMContentLoaded", function() {
    const dataset = document.getElementById('chat-container').dataset;
    const from = dataset.from;
    const gardenID = dataset.gardenId;
    if (gardenID != null) {
        const userId = dataset.ownerId;
        const firstName = dataset.firstName;
        const lastName = dataset.lastName;
        const email = dataset.email;
        showChatUI(userId, firstName, lastName, email);
    }

    // Attach event listeners to chat links
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

    // To send to, updated when a chat is opened
    let to;

    let stompClient = null;

    function connect() {
        // Determine the correct WebSocket URL based on the current environment
        let socketUrl;

        const url = new URL(window.location.href);
        const deployPath = getDeploymentContextPath(url);
        if (deployPath != null && deployPath.length > 0) {
            socketUrl = 'https://csse-seng302-team600.canterbury.ac.nz' + deployPath + '/ws'
        } else {
            // socketUrl = 'http://localhost:8080/ws';
            socketUrl = url.origin + '/ws'
        }

        // Create a WebSocket connection
        const socket = new WebSocket(socketUrl);

        //Create a Stomp client to send and receive messages
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);

            //Subscribe to the user's queue to receive messages
            stompClient.subscribe('/user/queue/reply', function (messageOutput) {
                // Show the message in the chat area
                showMessage(JSON.parse(messageOutput.body));
            });
        });
    }

    function sendMessage(userId) {
        const messageInput = document.getElementById('message-input-' + userId);
        const messageContent = messageInput.value.trim();

        if (messageContent && stompClient) {

            // Serialize the message to send this will become an Entity in the backend
            const chatMessage = {
                sender: from,
                recipient: to.email,
                content: messageContent,
                timestamp: new Date().toISOString()
            };
            // Send the message to the recipient's queue
            stompClient.send("/app/chat.send/" + to.email, {}, JSON.stringify(chatMessage));
            messageInput.value = '';
            let noMessagesText = document.getElementById("no-messages-text");
            if (noMessagesText) {
                noMessagesText.setAttribute('style', 'display: none !important;');
            }
            // Show the message in the chat area
            showMessage(chatMessage, userId);
        }

        messageLength(messageInput);
    }

    function showChatUI(userId, firstName, lastName, email) {
        to = {id: userId, email: email};

        // Hide the welcome message and other chat UIs
        const welcome = document.getElementById('welcomeMessage');
        welcome.setAttribute('style', 'display: none !important;');

        // Hide all chat UIs
        document.querySelectorAll('[id^="chatUI-"]').forEach(chatUI =>
            chatUI.setAttribute('style', 'display: none !important;'));

        //Show the current UI
        const currChat = document.getElementById('chatUI-' + userId);
        currChat.setAttribute('style', 'display: flex !important;');

        lastName = lastName ? lastName : '';
        document.getElementById('friendName-' + userId).innerText = firstName + ' ' + lastName;

        const deployPath = getDeploymentContextPath(null);
        document.getElementById('friendImage-' + userId).src = deployPath + '/get-image?view-user-profile=true&userID=' + userId;

        const messageInput = document.getElementById('message-input-' + userId);
        // document.getElementById('message-input-' + userId).focus();
        messageInput.focus();
        messageLength(messageInput);

        // Get all the past chats from the backend
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
                    messages.forEach(message => {
                        // Show all the messages
                        showMessage(message);
                    });
                } else {
                    chatArea.innerHTML = '<p id="no-messages-text" class="text-muted">No messages yet. Start the conversation!</p>';
                }
            });
    }

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

        const backgroundColor = isSender ? 'background' : 'bg-light' // Green if sender or gray if received

        // EACH MESSAGE HTML
        messageElement.innerHTML = `
            <div class="${messageClass} ${backgroundColor} p-3 rounded w-20 text-break">
                <strong>${message.sender}</strong>: ${message.content} <br>
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

    document.querySelectorAll('[id^="send-button-"]').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.id.split('-')[2];
            sendMessage(userId);
        });
    });

    // Connect to the WebSocket firstly
    connect();
});
