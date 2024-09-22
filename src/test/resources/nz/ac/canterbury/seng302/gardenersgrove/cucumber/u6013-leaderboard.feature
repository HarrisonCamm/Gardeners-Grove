Feature: Leaderboard Display

  As a user of the app
  I want to see a leaderboard with the top 10 users by blooms
  So that I can view my rank and how I compare to other users

  Background:
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And I am on the main page


  Scenario: Display top 10 users with highest number of blooms (AC1)
    Then I can see a table with the ten users of the app who have the highest number of blooms

  Scenario: View columns in the leaderboard table (AC2)
    Then I can see three columns in the leaderboard table
    And the first column is the rank
    And the second column is the profile picture and name
    And the third column is the number of blooms

  Scenario: Show current user placement outside top 10 (AC3)
    And I can see a table with the ten users of the app who have the highest number of blooms
    And I am not in the top 10, when I view the leaderboard table
    Then I can see my placing on the table at the bottom with my ranking out of all the users
