Feature: U6005 - Play plant guesser
  As Lei, I want to play a Plant Guesser game so I can guess the name of a plant based on the image, learn and earn Blooms.

  Background: User is logged in
    Given I am logged in with email "lei@email.com" and password "Password1!"
    And I am on the Games page

  Scenario: AC1 - Viewing game
    When I go to the Plant Guesser game page
    Then I see an image of a plant
    And I see four options of plant names where one is the correct plant name and the other three are names of plants in the same family to click on
    And I see a text description saying Plant X "/10"
    And I see a text description saying "What plant is this?"

  Scenario: AC3 - guessing correctly
    Given I go to the Plant Guesser game page
    When I select the correct plant name
    Then I am shown a message saying "You got it correct! +10 Blooms"
    And the option I selected is shown as green
    And a Next Question button (could be an icon) is shown

  Scenario: AC4 - going to next question
    Given I go to the Plant Guesser game page
    When I click the Next Question button after guessing
    Then I am shown a new image of a plant
    And I am shown four new options of plant names where one is the correct plant name and the other three are names of plants in the same family to click on
    And I see a text description saying Plant X+1 "/10"
    And I see a text description saying "What plant is this?"

  Scenario: AC5 - guessing incorrectly
    Given I go to the Plant Guesser game page
    When I select an incorrect plant name option
    Then I am shown a message saying "Wrong answer! The correct answer was: " correct plant name
    And the option I selected is shown as red
    And a Next Question button (could be an icon) is shown

  Scenario: AC6 - completing the game
    Given I go to the Plant Guesser game page
    When I have guessed for 10 plants (completed the game)
    Then an additional message is shown below any other messages "Congratulations on guessing! You have gained 100 Blooms for playing"
    And I see my score of correct guesses out of 10
    And my total Bloom count is updated and displayed

  Scenario: AC7 - exiting the game
    Given I go to the Plant Guesser game page
    When I click the Back button (could be an icon)
    Then I am taken back to the Games page
    And my total Blooms are displayed
    And my current game progress is not saved