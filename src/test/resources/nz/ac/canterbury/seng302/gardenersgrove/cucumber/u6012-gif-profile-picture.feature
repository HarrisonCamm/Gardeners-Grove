Feature: U6012 Gif Profile Picture

  Background: User is logged in
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I have a inventory with "Cat Typing" GIF profile item

  Scenario: AC1 - Replace current profile picture with GIF profile item
    Given I am in my inventory
    When I click on the "Use" button for the "Cat Typing" imageItem
    Then the "Cat Typing" gif replaces my current profile picture
    And my old profile picture is stored for later

 Scenario Outline: AC2 - Other Users can see my GIF profile image
   Given I have applied the "Cat Typing" GIF item
   And I am friends with "sarah@email.com"
   When I am logged in with email "sarah@email.com" and password "Password1!"
   And I views Liam's profile image on the <endpoint> page
   Then I can see the "Cat Typing" GIF in place of "liam@email.com"s old profile picture
   Examples:
     | endpoint |
     | "/messages" |
     | "/manage-friends" |

  Scenario: AC3
    Given I have applied the "Cat Typing" GIF item
    And I am on my profile page
    When I view my profile picture
    Then I can see the "Cat Typing" GIF image as my profile picture

  Scenario: AC4
    Given I have a friend who has applied the "Cat Typing" GIF image item
    When I view their profile
    Then I can see the gif displayed as their profile picture

  Scenario: AC5
    Given I am viewing a public garden
    And the owner has applied a gif image item to their profile picture
    When I view their profile picture
    Then I can see the gif they have selected as their profile picture
