Feature: Add tags to garden

  Background: User is logged in
    Given I am logged in with email "user@gmail.com" and password "p@ssw0rd123"

  Scenario: Owner adds tags to their garden
    Given I am a garden owner
    And I am on the garden details page for a garden I own to observe the tags feature
    Then there is a textbox where I can type in tags to the garden