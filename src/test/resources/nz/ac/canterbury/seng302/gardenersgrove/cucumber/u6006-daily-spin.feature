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

  Scenario: AC3 - Combos
    Given The randomisation seed is 1
    And I haven't used the daily spin today
    And The wheel spin animation has completed
    When I get a combo
    Then I am rewarded blooms based on the combo

  Scenario: AC5 – Pay for extra spins prompt
    Given The randomisation seed is 1
    And I haven't used the daily spin today
    And The wheel spin animation has completed
    And I have already used the daily spin for the day
    When I try to spin again
    Then I am shown a message that says, "You've already spun today! Spend 50฿ to spin again?"
    And The 50 cost is displayed on the spin button.

  Scenario: AC6 - Pay for extra spins
    Given The randomisation seed is 1
    And I have already used the daily spin for the day
    Given I pay for an extra spin
    Then 50 blooms is deducted from my account balance

  Scenario: AC7 – Exiting the game while running
    Given The randomisation seed is 1
    And I haven't used the daily spin today
    And I have already started a spin
    When I leave the page
    Then the spin outcome is still processed and the blooms are still added to my balance