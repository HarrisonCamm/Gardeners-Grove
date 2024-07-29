Feature: Add tags to garden

  Background: User is logged in
    Given I am logged in with email "startup@user.com" and password "Password1!"

  Scenario: Owner adds tags to their garden
    Given I am a garden owner
    And I am on the garden details page for a garden I own to observe the tags feature
    Then there is a text box where I can type in tags to the garden

  Scenario: View tags of a public garden
    Given I am on the garden details page for a public garden
    Then I can see a list of tags that the garden has been marked with by its owner

  Scenario: Autocomplete options for tags
    Given I have already created a tag for a garden I own
    When I have typed a tag into the text box that matches the tag I created
    Then I should see autocomplete options for tags that already exist in the system