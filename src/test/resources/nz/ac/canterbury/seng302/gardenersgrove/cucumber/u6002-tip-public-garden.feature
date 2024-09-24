Feature: U6002 Tipping public garden

  Background: User is logged in
    Given "inaya@email.com" has a public garden
    And "inaya@email.com"'s garden has been tipped 100 blooms by "liam@email.com"


  Scenario Outline: AC3 - Tip validation
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I am on the garden details page for a garden I do not own
    When I enter an invalid tip <tip>
    Then I am shown an error message "Tip amount must be a positive number"
    Examples:
    | tip       |
    | -100      |
    | 0         |
    | "invalid" |


  Scenario Outline: AC4 - Tip validation insufficient balance
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I am on the garden details page for a garden I do not own
    When I enter an invalid tip <tip>
    Then I am shown an error message "Insufficient bloom balance"
    Examples:
      | tip       |
      | 9999999999|
      | 1000000000|

  Scenario Outline: AC5 - Tip success
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I am on the garden details page for a garden I do not own
    And I enter an valid tip <tip>
    When I confirm the transaction for <tip> by clicking "Confirm"
    Then the Blooms are deducted from my account
    And the garden's tip count is updated
    Examples:
      | tip       |
      | -100      |
      | 0         |
      | "invalid" |
      | 9999999999|


  Scenario: AC6 - View total tipped Blooms
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And I am on the garden details page for a garden I own for tips
    Then I can see the total number of Blooms the garden has received as tips

  Scenario: AC7 -  Claim Blooms button
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And I have received tips for my garden for 100 blooms
    When I am on the garden details page for a garden I own for tips
    Then I see a claim blooms button to add the amount of unclaimed bloom tips of the garden to my balance