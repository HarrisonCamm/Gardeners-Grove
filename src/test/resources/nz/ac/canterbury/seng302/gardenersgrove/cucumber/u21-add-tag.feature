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
    When I type a tag into the text box that matches the tag I created
    Then I should see autocomplete options for tags that already exist in the system

  Scenario: AC4 - Add tag from autocomplete options
    Given I have typed a tag into the text box that matches the tag I created
    When I click on one suggestion
    Then the tag is added to my garden
    And the text box is cleared

  Scenario Outline: AC5 - Valid tag is added to garden
    Given I have entered valid text for a tag <tag> that does not exist
    When I click the + button or press enter
    Then the tag is added to my garden
    And the text box is cleared
    And the tag becomes a new user-defined tag on the system showing up in future auto-complete suggestions
    Examples:
      | tag            |
      | "tag"          |
      | "tag123"       |
      | "123"          |
      | "tag-tag"      |
      | "tag_tag1"     |
      | "tag tag"      |
      | "tag'1"        |
      | "tag -_- tag"  |


  Scenario Outline: AC6 - Error message for invalid tag input
    Given I have entered invalid text <tag>
    When I click the + button or press enter
    Then a tag error message <error type> tells me "The tag name must only contain alphanumeric characters, spaces, -, _, or '"
    And no tag is added to my garden
    And no tag is added to the user defined tags the system knows
    Examples:
      | tag            | error type     |
      | "!@#$%^&as"    | "tagTextError" |
      | "           "  | "tagTextError" |
      | "tag?"         | "tagTextError" |
      | "tag!"         | "tagTextError" |
      | "tag$"         | "tagTextError" |
      | "tag#"         | "tagTextError" |
      | "tag@"         | "tagTextError" |
      | "tag%"         | "tagTextError" |
      | "tag^"         | "tagTextError" |
      | "tag&"         | "tagTextError" |
      | "tag*"         | "tagTextError" |
      | "tag("         | "tagTextError" |
      | "tag)"         | "tagTextError" |
      | "tag+"         | "tagTextError" |
      | "tag="         | "tagTextError" |
      | "tag{"         | "tagTextError" |
      | "tag}"         | "tagTextError" |
      | "tag["         | "tagTextError" |
      | "tag]"         | "tagTextError" |
      | "tag/"         | "tagTextError" |
      | "tag<"         | "tagTextError" |
      | "tag>"         | "tagTextError" |
      | "tag."         | "tagTextError" |
      | "tag,"         | "tagTextError" |

  Scenario: AC7 - Error message for tag that is too long
    Given I have entered a tag that is more than 25 characters long
    When I click the + button or press enter
    Then a tag error message "tagLengthError" tells me "A tag cannot exceed 25 characters"
    And no tag is added to my garden
    And no tag is added to the user defined tags the system knows
