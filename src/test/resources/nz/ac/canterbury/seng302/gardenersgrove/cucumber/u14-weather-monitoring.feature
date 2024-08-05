Feature: Display Current Weather on Garden Details Page

  Background: User is logged in
    Given I am logged in with email "kaia@email.com" and password "Password1!"
    And I am a garden owner with in "Kerikeri" in "New Zealand"

  Scenario: AC1 - Owner views current weather on garden details page
    Given I am on the garden details page for a garden I own to check the weather
    When I look at the weather section
    Then the current day of the week is shown
    And the current date is shown
    And a description of the weather (i.e. sunny, overcast, raining) is shown with a relevant image
    And the current temperature is shown
    And the current humidity is shown