Feature: U20 - Browsing public gardens

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"

  @SingleGarden
  Scenario: AC1 - View a public garden's details
    Given a garden has been marked as public
    Then any logged-in user can view the name, size, and plants when clicking on a link to the garden

  @SingleGarden
  Scenario: AC2 - Browse gardens from anywhere in the system
    Given I am anywhere on the system
    When I click the "Browse Gardens" button on the navigation bar
    Then I am taken to a page with a search text box and the 10 or fewer of newest created gardens

  @SingleGarden
  Scenario: AC3 - Search for gardens by name or plants
    Given I enter a search string "TestPlant" into the search box
    When I click the search button labelled "Search" or the magnifying glass icon
    Then I am shown only gardens whose names or plants include "TestPlant"

  @SingleGarden
  Scenario: AC4 - Search results when pressing Enter
    Given I enter a search string "TestPlant" into the search box
    When I press the Enter key
    Then the results are shown as if I clicked the search button

  @SingleGarden
  Scenario: AC5 - No matching gardens found
    Given I enter a search string "unknown" that has no matches
    When I click the search button labelled "Search" or the magnifying glass icon
    Then a message tells me "No gardens match your search"

  @MultipleGardens
  Scenario: AC6 - Paginated search results
    Given I enter a search string "TestPlant" into the search box
    When there are more than 10 gardens
    Then the results are paginated with 10 per page

  @MultipleGardens
  Scenario: AC7 - Navigate to the first page of results
    Given I am on any page of results
    When I click "first" underneath the results
    Then I am taken to the first page

  @MultipleGardens
  Scenario: AC8 - Navigate to the last page of results
    Given I am on any page of results
    When I click "last" underneath the results
    Then I am taken to the last page

  @MultipleGardens
  Scenario: AC9 - Prevent navigating beyond first or last page
    Given I click any page navigation button
    Then I am never taken before the first page or beyond the last page

  @MultipleGardens
  Scenario: AC10 - View page numbers and links
    Given I am on page 2 with 10 results
    Then I should see links for pages 1, 2

  @MultipleGardens
  Scenario: AC11 - Navigate to a specific page
    Given I click on page number 2
    Then I am navigated to that page

  @MultipleGardens
  Scenario: AC12 - Display search results index
    Given I am on any page
    Then I see the text "Showing results 11 to 20 of 20"
