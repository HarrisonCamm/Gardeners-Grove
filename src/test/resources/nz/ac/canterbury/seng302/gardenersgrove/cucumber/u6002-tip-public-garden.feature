Feature: U6002 Tipping public garden

  Background: User is logged in
    Given "inaya@email.com" has a public garden
    And "liam@email.com" has 10000 Blooms
    And "inaya@email.com"'s garden has been tipped 100 blooms by "liam@email.com"


  Scenario Outline: AC3 - Tip validation
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I am on the garden details page for a garden I do not own
    When I enter an invalid tip <tip>
    And I confirm the transaction by clicking Confirm
    Then I am shown an error message "Tip amount must be a positive integer"
    Examples:
    | tip    |
    | -100   |
    | 0      |


  Scenario Outline: AC4 - Tip validation insufficient balance
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I am on the garden details page for a garden I do not own
    When I enter an invalid tip <tip>
    And I confirm the transaction by clicking Confirm
    Then I am shown an error message "Insufficient Bloom balance"
    Examples:
      | tip    |
      | 9999999|
      | 1000000|

  Scenario Outline: AC5 - Tip success
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I am on the garden details page for a garden I do not own
    When I enter an valid tip <tip>
    And I confirm the transaction by clicking Confirm
    Then the Blooms are deducted from my account
    And the garden's tip count is updated
    Examples:
      | tip    |
      | 5      |
      | 10     |

  Scenario: AC5 - Tip success (edge case)
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I am on the garden details page for a garden I do not own
    When I enter a valid tip that is my entire balance
    And I confirm the transaction by clicking Confirm
    Then the Blooms are deducted from my account
    And the garden's tip count is updated

  Scenario: AC6 - View total tipped Blooms
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And I am on the garden details page for a garden I own for tips
    Then I can see the total number of Blooms the garden has received as tips

  Scenario: AC7 -  Claim Blooms button
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And I have received tips for my garden
    When I am on the garden details page for a garden I own for tips
    Then I see a claim blooms button to add the amount of unclaimed bloom tips of the garden to my balance

#  Background: User is logged in
#    Given "inaya@email.com" has a public garden
#    And "liam@email.com" has 10000 Blooms
#    And "inaya@email.com"'s garden has been tipped 100 blooms by "liam@email.com"

  Scenario: AC8 - Claim Blooms transaction
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    When  I choose to claim the Blooms from my garden's tips
    Then the 100 blooms are added to my account
    And a transaction is added to my account history
    And the total number of Blooms I can claim is 0