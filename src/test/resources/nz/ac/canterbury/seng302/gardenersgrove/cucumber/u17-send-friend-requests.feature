Feature: U17 As Liam, I want to connect with my friends on Gardener’s Grove so that we can build a community on the app.
  Background: Logged in user
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I have no friends

  Scenario: AC1 - Viewing the Manage Friends page.
    Given I am anywhere on the app
    When I click on a UI element that allows me to send friend requests
    Then I am shown a manage friends page

  Scenario: AC2 - Viewing list of friends
    Given I am on the manage friends page
    Then I see the list of my friends with their names and their profile pictures
    And a link to their gardens list including private and public gardens

  Scenario: AC3 - Viewing Search Bar
    Given I am on the manage friends page
    When I hit the add friend button
    Then I see a search bar

  Scenario Outline: AC4 - Viewing list of users from name search
    Given I am on the manage friends page
    And I have opened the search bar
    When I enter a full name <name>
    And I hit the search button
    Then I can see a list of users of the app exactly matching the name I provided
    Examples:
      | name           |
      | "Kaia Pene"    |
      | "Inaya Singh"  |
      | "Lei Yuan"     |
      | "Sarah"        |

  Scenario Outline: AC5 - Viewing list of users from email search
    Given I am on the manage friends page
    And I have opened the search bar
    When I enter an email address <email>
    And I hit the search button
    Then I can see a list of users of the app exactly matching the email provided
    Examples:
      | email             |
      | "kaia@email.com"  |
      | "inaya@email.com" |
      | "lei@email.com"   |
      | "sarah@email.com" |

  Scenario Outline: AC6 - No perfect matches from search
    Given I am on the manage friends page
    And I have opened the search bar
    When I enter a search string <string>
    And I hit the search button
    And there are no perfect matches
    Then I see a message saying "There is nobody with that name or email in Gardener’s Grove"
    Examples:
      | string           |
      | "Bob"            |
      | "john@email.com" |
      | "123"            |
      | "user@email"     |

  Scenario Outline: AC7 - Adding a friend from search
    Given I enter a search string <string>
    And I hit the search button
    And I see a matching person for the search I made
    When I hit the invite as friend button
    Then the other user receives an invite that will be shown in their manage friends page
    Examples:
      | string            |
      | "Lei Yuan"        |
      | "Sarah"           |
      | "kaia@email.com"  |
      | "inaya@email.com" |

  Scenario Outline: AC8 - Accepting a friend request
    Given I am on the manage friends page
    And I have pending invites from <user>
    When I accept an invite
    Then that person is added to my list of friends
    And I can see their profile
    And I am added to that person’s friends list
    And they log into their account with <user> and <password>
    And that person can see my profile
    Examples:
      | user              | password     |
      | "kaia@email.com"  | "Password1!" |
      | "inaya@email.com" | "Password1!" |
      | "lei@email.com"   | "Password1!" |
      | "sarah@email.com" | "Password1!" |

  Scenario Outline: AC9 - Declining a friend request
    Given I am on the manage friends page
    And I have pending invites from <user>
    When I decline an invite
    Then that person is not added to my list of friends
    And they log into their account with <user> and <password>
    And they cannot invite me anymore
    Examples:
      | user              | password     |
      | "kaia@email.com"  | "Password1!" |
      | "inaya@email.com" | "Password1!" |
      | "lei@email.com"   | "Password1!" |
      | "sarah@email.com" | "Password1!" |

  Scenario Outline: AC10 - Viewing friend request status
    Given I have sent an invite to <user>
    And they log into their account with <user> and <password>
    And they leave or decline the invite <status>
    When I log in and check the status of the invite
    Then I can see the status of the invite as one of "Pending", or "Declined"
    Examples:
      | user              | status     | password     |
      | "kaia@email.com"  | ""         | "Password1!" |
      | "inaya@email.com" | ""         | "Password1!" |
      | "lei@email.com"   | "Declined" | "Password1!" |
      | "sarah@email.com" | "Declined" | "Password1!" |