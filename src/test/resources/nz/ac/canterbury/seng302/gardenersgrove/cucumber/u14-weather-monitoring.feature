Feature: Display Current Weather on Garden Details Page

  Background: User is logged in
    Given I am logged in with email "kaia@email.com" and password "Password1!"
    And I am a garden owner with in "Kerikeri" in "New Zealand"
    And I am a garden owner with an invalid location
    And I am a garden owner with a garden that has had no rain in the past two days
    And I am a garden owner with a garden that is in a location where it is currently raining

  Scenario: AC1 - Owner views current weather on garden details page
    Given I am on the garden details page for a garden I own to check the weather
    When I look at the weather section
    Then the current day of the week is shown
    And the current date is shown
    And a description of the weather (i.e. sunny, overcast, raining) is shown with a relevant image
    And the current temperature is shown
    And the current humidity is shown

  Scenario: AC2 - Display future weather forecast on garden details page
    Given I am on the garden details page for a garden I own to check the weather
    Then the future weather for the next 3 to 5 days is shown
    And the forecast includes the day of the week, date, weather description with a relevant image, temperature and humidity

  Scenario: AC3 - Display error message when garden location can't be found
    Given I am on the garden details page for a garden I own that is in an invalid location
    Then an error message is displayed saying "Location not found, please update your location to see the weather"

  Scenario: AC4 - Display watering reminder when there hasn't been any rain recently
    Given I am on the garden details page for a garden that hasn't had rain in the past two days
    Then a highlighted element tells me "There hasn’t been any rain recently, make sure to water your plants if they need it"

  Scenario: AC5 - Display rainy day reminder on garden details page
    Given I am on the garden details page for a garden that is in a location where it is currently raining
    Then a highlighted element tells me "Outdoor plants don’t need any water today"


