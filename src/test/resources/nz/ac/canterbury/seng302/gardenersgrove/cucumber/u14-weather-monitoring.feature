Feature: U14 Weather Monitoring

  Scenario: AC1 - Viewing current weather
    Given I am on the garden details page for a garden I own
    Then the current weather for my location is shown
      | Day of the Week | Date       | Description | Image  | Temperature | Humidity |
      | Monday          | 01/02/2022 | Sunny       | sunny.png | 25°C      | 50%      |

  Scenario: AC2 - Viewing future weather
    Given I am on the garden details page for a garden I own
    Then the future weather for the next 3 to 5 days is shown
      | Day of the Week | Date       | Description | Image    | Temperature | Humidity |
      | Tuesday         | 02/02/2022 | Overcast    | overcast.png | 20°C    | 60%      |
      | Wednesday       | 03/02/2022 | Rainy       | rainy.png | 18°C      | 70%      |

  Scenario: AC3 - Location not found
    Given the garden has a location that can’t be found
    Then an error message tells me "Location not found, please update your location to see the weather"

  Scenario: AC4 - No recent rain warning
    Given the past two days have been sunny
    When I am on my garden details page
    Then a highlighted element tells me "There hasn’t been any rain recently, make sure to water your plants if they need it"

  Scenario: AC5 - Rainy day advice
    Given the current weather is rainy
    When I am on my garden details page
    Then a highlighted element tells me "Outdoor plants don’t need any water today"

  Scenario: AC6 - Dismissing watering advice
    Given an element tells me I should or shouldn’t water my plants
    When I click the “x” or “close” button at the top right of the element
    Then the element is dismissed and does not show up for that garden until the next day

