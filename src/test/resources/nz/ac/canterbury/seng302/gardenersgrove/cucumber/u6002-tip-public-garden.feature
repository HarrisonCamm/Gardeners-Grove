Feature: U6002 Tipping public garden

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"



  Scenario: AC6 - View total tipped Blooms
    Given I am on the garden details page for a public garden
    Then I can see the total number of Blooms the garden has received as tips