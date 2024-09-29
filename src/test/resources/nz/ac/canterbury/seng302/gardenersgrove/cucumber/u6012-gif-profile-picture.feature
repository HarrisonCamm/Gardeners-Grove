Feature: U6012 Gif Profile Picture

  Background: User is logged in
    Given I am logged in with email "liam@email.com" and password "Password1!"
    And I have a inventory with "Cat Typing" GIF profile item

  Scenario: AC1 - Replace current profile picture with GIF profile item
    Given I am in my inventory
    When I click on the "Use" button for the "Cat Typing" imageItem
    Then the "Cat Typing" gif replaces my current profile picture
    And my old profile picture is stored for later