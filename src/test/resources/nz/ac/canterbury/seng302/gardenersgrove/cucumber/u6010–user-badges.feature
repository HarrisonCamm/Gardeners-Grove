Feature: User Badges
  As Sarah, I want to personalize my Gardener's Grove experience by applying badges (emojis) next to my name, so that I can showcase my achievements and express my personality to others.

  Background: User is logged in
    Given "inaya@email.com" has 10000 Blooms
    And I am logged in with email "inaya@email.com" and password "Password1!"
    And I have items in my inventory

  Scenario: AC1 - Apply badge to user name
    Given I am in my inventory and own a badge item
    When I click on the Use button on that badge item
    Then the badge is shown next to my name

  Scenario: AC2 - View another user's badge next to their name
    Given I have a badge item applied to my name
    When another user views my name
    Then they see the badge displayed next to my name

  Scenario: AC3 - View badge on profile page
    Given I have a badge item applied to my name
    When I am on my profile page
    And I view my name
    Then the badge I have applied are displayed next to my name

  Scenario: AC4 - View badge on a friend's profile page
    Given I have a friend "liam@email.com" and they have a badge item applied to their name
    When I view their profile
    Then I see the badges displayed next to their name

  Scenario: AC5 - View badge in friends search results
    Given I am on the friends page and open the add friends search bar
    When I search for a friend who has a badge item applied to their name
    Then I see the badge displayed next to their name in the search results

  Scenario: AC6 - View badge for a friend with a pending invite
    Given I am on the friends page and I have a pending invite from a friend who has a badge item applied to their name
    When I view their name
    Then I see the badge next to their name

  Scenario: AC7 - View badge for a friend I have sent an invite to
    Given I am on the friends page and I have sent an invite to a friend who has a badge item applied to their name
    When I view their name
    Then I see the badge next to their name

  Scenario: AC8 - View badge in a public garden
    Given I am viewing a public garden and the owner has a badge item applied to their name
    When I view their name
    Then I see the badge displayed next to their name

  Scenario: AC9 - Unapply badge from user name
    Given I have a badge item applied to my name
    And I am in my inventory
    When I click the "Unapply" button on that badge item
    Then the badge is removed from my name
