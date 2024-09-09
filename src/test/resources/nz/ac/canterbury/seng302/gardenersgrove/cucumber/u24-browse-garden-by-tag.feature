Feature: U24 - Browsing gardens by tag
  As Inaya, I want to be able to browse gardens by different tags so that I can browse for gardens that match my interests.

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"

  Scenario: AC1 - Browse by multiple tags
    Given I am browsing gardens
    Then I can select any number of tags to filter by

  Scenario: AC2 - Tag autocomplete
    Given I want to browse for a tag
    When I start typing the tag
    Then tags matching my input are shown

  Scenario: AC3 - Clicking on tag from autocomplete
    Given I am viewing autocomplete suggestions for my input
    When I click on a suggestion
    Then the tag is added to my current selection
    And the text field is cleared

  Scenario: AC4 - Typing tag that exists
    Given I type out a tag that already exists
    When I press the enter key
    Then the tag is added to my current selection
    And the text field is cleared

  Scenario Outline: AC5 - Typing tag that does not exist
    Given I type out a tag that does not exist
    When I press the enter key
    Then no tag is added to my current selection
    And the text field is not cleared
    And an error message tells me No tag matching <input>
    Examples:
    | input  |
    | "tag"  |

  Scenario: AC6 - Submit search form
    Given I submit the search form as detailed in U17
    Then only gardens that match the other search requirements and any of the tags I selected are shown in the results