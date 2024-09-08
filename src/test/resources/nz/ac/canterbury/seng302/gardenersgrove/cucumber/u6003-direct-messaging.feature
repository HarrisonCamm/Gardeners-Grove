Feature: U6003 - Direct Messaging

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And "inaya@email.com" is friends with "sarah@email.com"

  Scenario: AC1 - Messages Button
    Given I am anywhere in the system
    When I click on the "Messages" button
    Then I can see all my friends with the last message they sent next to their name

  Scenario: AC2 - Opening Friends Chat
    Given I click on the "Messages" button
    When I click on a UI element that has the friends profile picture, name, and last message
    Then I am taken to the chat to start or continue a conversation

  # SKIP AC3 AS WE PROBABLY NEED TO GET RID OF IT

  Scenario: AC4 - Sending a Message
    Given I click on a UI element that has the friends profile picture, name, and last message
    When I send a message to another user
    Then they are able to see the message in real time and my message appears on the right

  Scenario: AC5 - Receiving a Message
    Given I send a message to another user
    When they receive the message
    Then they are able to see the message in real time and my message appears on the left

  Scenario: AC6 - Content Moderation
    Given I send a message to another user
    When when I enter inappropriate words and click send
    Then then that message is not sent, and I am shown the message "Message contains inappropriate language"


