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
    Examples:
      | user              | password     |
      | "kaia@email.com"  | "Password1!" |
      | "inaya@email.com" | "Password1!" |
      | "startup@user.com"   | "Password1!" |

    Scenario Outline: AC2 - Remove a friend from my list
      Given I am on the manage friends page
      And I have pending invites from <friend>
      And I accept an invite
      And I see my friends list has <friend>
      When I click on a UI element that allows me to remove a <friend> from my list
      And I confirm that I want to remove <friend>
      Then <friend> is removed from my list of friends
      And I cannot see <friend>'s gardens
      And <friend> cannot see my gardens
      And I am removed from the list of friends of <friend>
      Examples:
        | friend            |
        | "kaia@email.com"  |
        | "inaya@email.com" |
        | "lei@email.com"   |
        | "sarah@email.com" |

