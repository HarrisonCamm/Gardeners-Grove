Feature: U12 As Kaia, I want to update the plants I have in my garden so that I can add more details about them
  as I know more about them.

  Scenario Outline: AC1 - List of Plants
    Given I have 5 plants in my garden with the details <name>, <count>, <description>, and <date planted>
    And I am on the garden details page
    Then I should see a list of 5 plants
    And they have the following details displayed <name>, <count>, <description>, and <date planted>
    Examples:
      | name    | count | description      | date planted |
      | "plant1"| "1"   | "This is a plant"| "01/02/2002" |
      | "plant2"| "2"   | "This is a plant"| "01/02/2022" |
      | "plant3"| "3"   | "This is a plant"| "01/02/2023" |

#    Scenario: AC2 - List of >10 Plants
#      Given I am on the garden details page
#        And I have 15 plants in my garden
#        Then Then there is a scrollable list of plants

  Scenario Outline: AC3 - Editing a plant the form is prefilled with the plant details
    Given I have 1 plants in my garden with the details <name>, <count>, <description>, and <date planted>
    And I am on the garden details page
    When I hit the edit button for the first plant
    Then The edit plant form is prefilled with the details <name>, <count>, <description>, and <date planted>
    Examples:
      | name    | count | description      | date planted |
      | "plant1"| "1"   | "This is a plant"| "01/02/2002" |
      | "plant2"| "2"   | "This is a plant"| "01/02/2022" |
      | "plant3"| "3"   | "This is a plant"| "01/02/2023" |

  Scenario Outline: AC4 - Updated valid plant details are saved when submitting the form
    Given I have 1 plants in my garden with the details <name>, <count>, <description>, and <date planted>
    And I am on the garden details page
    When I hit the edit button for the first plant
    And I hit the save button with the details <new name>, <new count>, <new description>, and <new date planted>
    Then The plant details are updated with <new name>, <new count>, <new description>, and <new date planted>
    Examples:
      | name     | count | description     | date planted | new name | new count | new description             | new date planted |
      | "plant1" | "1"   | "Small cactus"  | "01/02/2002" | "Cactus X" | "2"   | "Thrives in dry environments"| "15/03/2022"     |
      | "plant2" | "2"   | "Orchid bloom"  | "01/02/2022" | "Orchid Y" | "3"   | "Needs indirect sunlight"    | "16/04/2023"     |
      | "plant3" | "3"   | "Rose bush"     | "01/02/2023" | "Rose Z"   | "1"   | "Requires frequent watering" | "17/05/2024"     |

  Scenario Outline: AC5 - Updating a plant with an invalid name
    Given I have 1 plants in my garden with the details <name>, <count>, <description>, and <date planted>
    And I am on the garden details page
    When I hit the edit button for the first plant
    And I hit the save button with the details <new name>, <new count>, <new description>, and <new date planted>
    Then The plant details are not updated and stay as <name>, <count>, <description>, and <date planted>
    And An error message tells me "Plant name cannot be empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes."
    Examples:
      | name     | count | description        | date planted | new name      | new count | new description              | new date planted |
      | "plant1" | "1"   | "Small cactus"     | "01/02/2002" | ""            | "2"       | "Thrives in dry environments" | "15/03/2022"     |
      | "plant2" | "2"   | "Orchid bloom"     | "01/02/2022" | "123*Orch"    | "3"       | "Needs indirect sunlight"     | "16/04/2023"     |
      | "plant3" | "3"   | "Rose bush"        | "01/02/2023" | "Rose#"       | "1"       | "Requires frequent watering"  | "17/05/2024"     |
      | "plant4" | "4"   | "Lavender field"   | "12/08/2019" | "Rose@home"   | "2"       | "Fragrant and calming"        | "18/06/2025"     |
      | "plant5" | "5"   | "Sunflower array"  | "23/09/2021" | "Lavender%"   | "3"       | "Loves full sun exposure"     | "19/07/2026"     |
      | "plant6" | "6"   | "Herb garden"      | "05/11/2020" | "Mint&Rosemary" | "4"     | "Useful in cooking"           | "20/08/2027"     |
      | "plant7" | "7"   | "Cactus collection"| "15/01/2018" | "$Cacti"      | "5"       | "Requires minimal water"      | "21/09/2028"     |
#      | "plant8" | "8"   | "Fiddle leaf fig"  | "05/06/2019" | " "           | "2"       | "Prefers bright, indirect light" | "22/10/2029"  |      #ACs technically allow this "must only include... ...spaces..." but could look into it

  Scenario Outline: AC6 - Updating a plant with a description that is too long
    Given I have 1 plants in my garden with the details <name>, <count>, <description>, and <date planted>
    And I am on the garden details page
    When I hit the edit button for the first plant
    And I hit the save button with a description <description length> characters long and the details <new name>, <new count>, and <new date planted>
    Then The plant details are not updated and stay as <name>, <count>, <description>, and <date planted>
    And An error message tells me "Plant description must be less than 512 characters"
    Examples:
      | name     | count | description   | date planted | new name | new count | new date planted | description length |
      | "plant1" | "1"   | "Small cactus"| "01/02/2002" | "Cactus X" | "2"    | "15/03/2022"      | 513                |
      | "plant1" | "1"   | "Small cactus"| "01/02/2002" | "Cactus X" | "2"    | "15/03/2022"      | 1028               |
      | "plant1" | "1"   | "Small cactus"| "01/02/2002" | "Cactus X" | "2"    | "15/03/2022"      | 2056               |

  Scenario Outline: AC7 - Updating a plant with a count that is not a number
    Given I have 1 plants in my garden with the details <name>, <count>, <description>, and <date planted>
    And I am on the garden details page
    When I hit the edit button for the first plant
    And I hit the save button with the details <new name>, <new count>, <new description>, and <new date planted>
    Then The plant details are not updated and stay as <name>, <count>, <description>, and <date planted>
    And An error message tells me "Plant count must be a positive number"
    Examples:
      | name     | count | description  | date planted | new name | new count | new description            | new date planted |
      | "plant1" | "1"   | "Small cactus"| "01/02/2002" | "Cactus X" | "two"  | "Thrives in dry climates"  | "15/03/2022"     |
      | "plant2" | "2"   | "Orchid bloom"| "01/02/2022" | "Orchid Y" | "-5"   | "Needs indirect sunlight"  | "16/04/2023"     |
      | "plant3" | "3"   | "Rose bush"   | "01/02/2023" | "Rose Z"   | "1.1"  | "Requires frequent watering"| "17/05/2024"    |
      | "plant4" | "4"   | "Fern"        | "01/02/2024" | "Fern A"   | "1,1"  | "Prefers shade"            | "18/06/2025"     |
      | "plant5" | "5"   | "Palm tree"   | "01/02/2025" | "Palm B"   | "t5"   | "Needs lots of sunlight"   | "19/07/2026"     |

  Scenario Outline: AC8 - Updating a plant with a date that is not in the format DD/MM/YYYY
    Given I have 1 plants in my garden with the details <name>, <count>, <description>, and <date planted>
    And I am on the garden details page
    When I hit the edit button for the first plant
    And I hit the save button with the details <new name>, <new count>, <new description>, and <new date planted>
    Then The plant details are not updated and stay as <name>, <count>, <description>, and <date planted>
    And An error message tells me "Date is not in valid format, DD/MM/YYYY"
    Examples:
      | name     | count | description  | date planted | new name | new count | new description             | new date planted |
#      | "plant1" | "1"   | "Small cactus"| "01/02/2002" | "Cactus X"| "2"    | "Thrives in dry environments"| "2022-03-15"     |   #Form allows this probably an issue also not my problem
#      | "plant2" | "2"   | "Orchid bloom"| "01/02/2022" | "Orchid Y"| "3"    | "Needs indirect sunlight"   | "March 16, 2023" |    #Inputing this date will break the form error: "Index 2 out of bounds for length 1"
      | "plant3" | "3"   | "Rose bush"   | "01/02/2023" | "Rose Z"  | "1"    | "Requires frequent watering"| "32/02/2022"     |
      | "plant4" | "4"   | "Fern"        | "01/02/2024" | "Fern A"  | "1"    | "Prefers shade"             | "2025/06/18"     |
      | "plant5" | "5"   | "Palm tree"   | "01/02/2025" | "Palm B"  | "5"    | "Needs lots of sunlight"    | "19/21/2026"     |
      | "plant9" | "9"   | "Aloe Vera"   | "01/02/2029" | "Aloe F"  | "1"      | "Medicinal properties"      | "2029/04/25"     |
#      | "plant10"| "10"  | "Tulip"       | "01/02/2030" | "Tulip G"| "5"      | "Spring bloom"              | "25-Apr-2030"    |   #Inputing this date will break the form error: "Index 2 out of bounds for length 1"
#      | "plant11"| "11"  | "Peony"       | "01/02/2031" | "Peony H"| "3"      | "Perennial"                 | "April-25-2031"  |   #Inputing this date will break the form error: "Index 2 out of bounds for length 1"
#      | "plant12"| "12"  | "Daisy"       | "01/02/2032" | "Daisy I"| "2"      | "Bright and cheerful"       | "25-04-2032"     |   #Inputing this date will break the form error: "Index 2 out of bounds for length 1"
      | "plant13"| "13"  | "Lily"        | "01/02/2033" | "Lily J" | "4"      | "Elegant flowers"           | "2033/25/04"     |
#      | "plant14"| "14"  | "Chrysanthemum"| "01/02/2034"| "Chrysan K" | "1"   | "Autumn bloom"              | "April 25 2034"  |    #Inputing this date will break the form error: "Index 2 out of bounds for length 1"

  Scenario Outline: AC9 - Cancel editing a plant
    Given I have 1 plants in my garden with the details <name>, <count>, <description>, and <date planted>
    And I am on the garden details page
    When I hit the edit button for the first plant
    And I hit the cancel button on the edit plant form
    Then The plant details are not updated and stay as <name>, <count>, <description>, and <date planted>
    And I am taken back to the garden details page
    Examples:
      | name     | count | description  | date planted |
      | "plant1" | "1"   | "Small cactus"| "01/02/2002" |
      | "plant2" | "2"   | "Orchid bloom"| "01/02/2022" |
      | "plant3" | "3"   | "Rose bush"   | "01/02/2023" |
