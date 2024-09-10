Feature: U24 - Browsing gardens by tag
  As Inaya, I want to be able to browse gardens by different tags so that I can browse for gardens that match my interests.

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And I am a garden owner
    And there are public gardens with tags available
    And I am on the browse gardens page

  Scenario: AC1 - Browse by multiple tags
    Then I can select any number of tags to filter by

  Scenario: AC2 - Tag autocomplete
    Given I want to browse for a tag
    When I start typing the tag
    Then tags matching my input are shown

  Scenario Outline: AC3 - Clicking on tag from autocomplete
    Given I am viewing autocomplete suggestions for my input <input>
    When I click on a suggestion <input>
    Then the tag <input> is added to my current selection
    And the text field is cleared
    Examples:
      | input              |
      | "tagAutocomplete"  |

  Scenario Outline: AC4 - Typing tag that exists
    Given I type out a tag <input> that already exists
    When I press the enter key with <input>
    Then the tag <input> is added to my current selection
    And the text field is cleared
    Examples:
      | input         |
      | "tagValid"    |
      |"inaya garden" |
      | "herbal"      |
    #note the above input examples match the tags added in the
    # 'there_are_public_gardens_with_tags_available' step def, as Background can't have examples


  Scenario Outline: AC5 - Typing tag that does not exist
    Given I type out a tag <input> that does not exist
    When I press the enter key with <input>
    Then no tag <input> is added to my current selection
    And the text field contains <input> and is not cleared
    And an error message tells me No tag matching <input>
    Examples:
    | input         |
    | "tagInvalid"  |
    | "blah blah"   |

  Scenario: AC6 - Submit search form
    Given I submit the search form as detailed in U17
    Then only gardens that match the other search requirements and any of the tags I selected are shown in the results

  Scenario Outline: Removing a tag
    Given I press the enter key with <input>
    And the tag <input> is added to my current selection
    When I click the x button on the tag <input>
    Then no tag <input> is added to my current selection
    Examples:
      | input         |
      | "tagValid"    |
      |"inaya garden" |
      | "herbal"      |
    #note the above input examples match the tags added in the
    # 'there_are_public_gardens_with_tags_available' step def, as Background can't have examples
