Feature: U6001 - Showing Blooms Balance and Bloom Transactions

  As Kaia,
  I want to be able to see my Bloom balance,
  So that I know what I can buy and tip.
  Background:
    Given I am logged in with email "kaia@email.com" and password "Password1!"


  Scenario Outline: AC1 - Viewing Blooms
    When I navigate to any page <endpoint> in the system
    Then I can see my Bloom balance displayed prominently in the header or a dedicated section
    Examples:
      | endpoint          |
      | "/main"           |
      | "/view-gardens"   |
      | "/view-user-profile"   |
      | "/manage-friends" |
      | "/create-garden"  |


  Scenario: AC2 - Viewing Blooms Balance and Transaction History on Profile Page
    When I navigate to my profile page
    Then I can see my Bloom balance displayed prominently in the header or a dedicated section
    And I can see a detailed transaction history for the Bloom currency
    And the transaction history should be paginated or scrollable if it exceeds a certain number of entries

  Scenario: AC4 - No Transaction History Available
    Given I am a new user or have not made any transactions
    When I navigate to my profile page
    Then I should see a message indicating that no transaction history is available
    And I should see a brief description of how to earn or spend Blooms

  Scenario: AC3 - Transaction Details
    Given there are existing transactions
    And I am viewing the transaction history on my profile page
    When I click on a specific transaction
    Then I can see additional details for that transaction, if available

