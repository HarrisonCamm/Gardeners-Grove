Feature: U21 Add tags to garden

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And I am a garden owner

  Scenario: AC1 - Owner adds tags to their garden
    Given I am on the garden details page for a garden I own to observe the tags feature
    Then there is a text box where I can type in tags to the garden

  Scenario: AC2 - View tags of a public garden
    Given I am on the garden details page for a public garden
    Then I can see a list of tags that the garden has been marked with by its owner

  Scenario: AC3 - Autocomplete options for tags
    Given I have already created a tag for a garden I own
    When I have typed a tag into the text box that matches the tag I created
    Then I should see autocomplete options for tags that already exist in the system

  Scenario: AC4 - Add tag from autocomplete options
    Given I have typed a tag into the text box that matches the tag I created
    When I click on one suggestion
    Then that tag should be added to my garden and the text box cleared

  Scenario: AC5 - Error message for invalid tag input
    Given I have entered invalid text
    When I click the "+" button or press enter
    Then an error message tells me "The tag name must only contain alphanumeric characters, spaces, -, _, ', or ” , and no tag is added to my garden and no tag is added to the user defined tags the system knows