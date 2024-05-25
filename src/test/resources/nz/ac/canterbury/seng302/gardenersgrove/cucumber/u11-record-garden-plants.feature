Feature: U11 As Kaia, I want to record the different plants in my garden so I can keep track of the plants I have

  Scenario: AC1 - Viewing add plant form
    Given I am on the garden details page for a garden I own
    When I click the add new plant button
    Then I see an add plant form

  Scenario Outline: AC2 - Adding a plant to a garden with valid values
    Given I am on the add plant form
    And I enter valid values for the <name> and optionally a <count>, <description>, and a <date planted>
    When I click the submit button on the add plant form
    Then A new plant record is added to the garden
    And I am taken back to the garden details page from add plant page
    Examples:
      | name    | count | description      | date planted |
      | "plant1"| "1"   | "This is a plant"| "01/02/2002" |
      | "plant2"| ""    | "This is a plant"| "01/02/2022" |
      | "plant3"| "3"   | ""               | "01/02/2023" |
      | "plant4"| "1"   | "This is a plant"| ""           |
      | "plant5"| ""    | ""               | "01/02/2022" |
      | "plant6"| "3"   | ""               | ""           |
      | "plant7"| ""    | "This is a plant"| ""           |
      | "plant8"| ""    | ""               | ""           |

  Scenario Outline: AC3 - Adding a plant to a garden with invalid name
    Given I am on the add plant form
    And I enter an empty or invalid plant <name>
    When I click the submit button on the add plant form
    Then An error message tells me "Plant name cannot be empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes." on the add plant form
    Examples:
      | name     |
      | ""       |
      | "plant #"|
#      | "."      | i think these shouldn't be allowed but it hasn't been implemented in the validator yet
#      | "-"      |
#      | ","      |
      | "@plant" |
#      | "'"      |
      | "plant*" |

  Scenario Outline: AC4 - Adding a plant to a garden with a too long description
    Given I am on the add plant form
    And I enter a <description> that is longer than 512 characters
    When I click the submit button on the add plant form
    Then An error message tells me "Plant description must be less than 512 characters" on the add plant form
    Examples:
      | description     |
      | "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"|

  Scenario Outline: AC5 - Adding a plant to a garden with invalid count
    Given I am on the add plant form
    And I enter an invalid <count>
    When I click the submit button on the add plant form
    Then An error message tells me "Plant count must be a positive number" on the add plant form
    Examples:
      | count     |
      | "-0"      |
      | "-1"      |
      | "1."      |
      | "1..1"    |
      | "1.,1"    |
      | "0.0"     |
      | "1.1.1"   |
      | "1.1,1"   |
      | "0.0.0"   |
      | "1,1,1"   |
      | "1.0,1"   |
      | ".0"      |
      | "-.1"     |
      | ",0"      |
      | "1,."     |
      | "1,"      |

  Scenario Outline: AC6 - Adding a plant to a garden with invalid date
    Given I am on the add plant form
    And I enter a <date planted> that is not in the Aotearoa NZ format
    When I click the submit button on the add plant form
    Then An error message tells me "Date is not in valid format, DD/MM/YYYY" on the add plant form
    Examples:
      | date planted |
      | "00/00/0000" |
      | "12/"        |
      | "12/31/2000" |
      | "2000/12/10" |

  Scenario: AC7 - Cancel recording a plant
    Given I am on the add plant form
    When I click the cancel button on the add plant form
    Then I am taken back to the garden details page from add plant page
    And No changes are made to the garden

