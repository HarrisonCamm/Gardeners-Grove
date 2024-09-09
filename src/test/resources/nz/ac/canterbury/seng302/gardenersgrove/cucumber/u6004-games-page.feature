Feature: Games Page
  As Lei, I want to view available games so I can have a fun, educational experience and earn Blooms.

  Background: User is logged in
    Given I am logged in with email "lei@email.com" and password "Password1!"

  Scenario: AC1 - Viewing the Games page
    When I click the 'Games' button on the navbar
    Then I am taken to the Games page
    And I should see the game "Plant Guesser" with a description
    And I should see a "Play" button

  Scenario Outline: AC2 -
    Given I am on the Games page
    When I click the Play button for a game <game>
    Then I am taken to a page displaying the game to play
    And my total Blooms are displayed
    Examples:
      | game            |
      | "/plant-guesser" |
      | "/daily-spin"    |
