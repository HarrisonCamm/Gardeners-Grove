Feature: U6006 Daily Spin / Photosyntheslots

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"


  Scenario: AC1 - Navigate to daily spin
    Given I am on the main page
    Then I see a "Daily Spin" button prominently displayed on the navbar


  Scenario: AC2 - Daily Spin animation
    Given I am on the main page
    When I click the Daily Spin button on the navbar,
    Then I am taken to the Daily Spin Page
    And A spin wheel animation appears with garden themed emojis