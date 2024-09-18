Feature: U6002 Tipping public garden

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"



  Scenario: AC6 - View total tipped Blooms
    Given I am on the garden details page for a garden I own for tips
    Then I can see the total number of Blooms the garden has received as tips

  Scenario: AC7 -  Claim Blooms button
    Given I have received tips for my garden for 100 blooms
    When I am on the garden details page for a garden I own for tips
    Then I see a claim blooms button to add the amount of unclaimed bloom tips of the garden to my balance