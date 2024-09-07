Feature: Games Page
  As a user, I want to view available games so I can have a fun, educational experience and earn Blooms.

  Scenario: Viewing the Games page
    Given I am logged in
    When I click the 'Games' button on the navbar
    Then I am taken to the Games page
    And I should see the game "Plant Guesser" with a description
    And I should see a "Play" button
