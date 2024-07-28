Feature: Add tags to garden

  Scenario: Owner adds tags to their garden
    Given I am a garden owner
    And I am on the garden details page for a garden I own to observe the tags feature
    Then there is a textbox where I can type in tags to the garden