Feature: U18 As Liam, I want to cancel friends on Gardener’s Grove so that we can manage my friends list with people I trust.
  Background: Logged in user
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I have no friends



  Scenario Outline: AC1 - Cancelling a pending friend request
    Given I have sent an invite to <user>
    And <user> has not yet accepted the invite
    When I cancel my friend request
    Then <user> cannot see the friend request
    And <user> cannot accept the friend request
    Examples:
      | user              |
      | "kaia@email.com"  |
      | "inaya@email.com" |
      | "lei@email.com"   |
      | "sarah@email.com" |