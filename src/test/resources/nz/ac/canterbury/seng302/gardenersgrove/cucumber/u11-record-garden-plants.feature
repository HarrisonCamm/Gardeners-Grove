Feature: U11 As Sarah, I want to record plants, so I can see them in my garden.

  Scenario: AC1 - Viewing Add Plant Form
    Given I am on a garden details page for a garden I own
    When I click the add new plant button
    Then I see an add plant form