Feature: U22 - Tag moderation
  As Kaia, I want to make sure that tags added to gardens do not contain any inappropriate words so that the sensibilities of other gardeners are not offended.

  Background: User is logged in
    Given I am logged in with email "kaia@email.com" and password "Password1!"
    And I am a garden owner
    And RAR

  Scenario: AC1 - Checking for offensive or inappropriate words in valid tags
    Given I am adding a valid tag
    When I confirm the tag
    Then the tag is checked for offensive or inappropriate words

  Scenario: AC2 - Handling inappropriate tags
    Given I am adding a innapropriate tag
    When I confirm the tag
    Then the tag is checked for offensive or inappropriate words
    And an error message tells me that the submitted word is not appropriate
    And the tag is not added to the list of user-defined tags

  Scenario: AC3 - Handling tags that cannot be evaluated immediately
    Given the submitted tag cannot be evaluated for appropriateness
    Then the tag is not visible publicly
    And it is added to a waiting list that will be evaluated as soon as possible

  Scenario: AC4 - Making delayed evaluated tags visible
    Given the evaluation of a user-defined tag was delayed
    When the tag has been evaluated as appropriate
    Then the tag is visible publicly on the garden it was assigned to
    And it is added to the list of user-defined tags

  Scenario: AC5 - Handling inappropriate tags after delayed evaluation
    Given the evaluation of a user-defined tag was delayed
    When the tag has been evaluated as inappropriate
    Then the tag is removed from the garden it was assigned to
    And it is not added to the list of user-defined tags
    And the user’s count of inappropriate tags is increased by 1





