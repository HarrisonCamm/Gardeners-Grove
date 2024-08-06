Feature: U18 As Liam, I want to cancel friends on Gardener’s Grove so that we can manage my friends list with people I trust.
  Background: Logged in user
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I have no friends



  Scenario Outline: AC1 - Cancelling a pending friend request
    Given I have sent an invite to <user>
    And they log into their account with <user> and <password>
    And <user> has not yet accepted or declined the invite
    When I cancel my friend request
    Then <user> cannot see the friend request
    And <user> cannot accept the friend request
    Examples:
      | user              | password     |
      | "kaia@email.com"  | "Password1!" |
      | "inaya@email.com" | "Password1!" |
      | "lei@email.com"   | "Password1!" |
      | "sarah@email.com" | "Password1!" |