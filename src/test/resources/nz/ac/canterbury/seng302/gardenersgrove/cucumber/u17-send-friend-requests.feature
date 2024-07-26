Feature: U17 As Liam, I want to connect with my friends on Gardener’s Grove so that we can build a community on the app.

  Scenario: AC1 - Viewing the Manage Friends page.
    Given I am anywhere on the app
    When I click on a UI element that allows me to send friend requests
    Then I am shown a “manage friends” page

  Scenario: AC2 - Viewing list of friends
    Given I am on the manage friends page,
    Then I see the list of my friends with their names
    And their profile pictures
    And a link to their gardens list including private and public gardens

  Scenario: AC3 - Viewing Search Bar
    Given I am on the manage friends page
    When I hit the add friend button
    Then I see a search bar