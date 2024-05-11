Feature: U16 As Sarah, I want to be able to change my password over email, so that I can still access my account
  even if I forget my password.

  Scenario: AC1 - Accessing the password recovery form
    Given I am on the login page
    When I hit the "Forgot your password?" link
    Then I see a form asking me for my email address

  Scenario Outline: AC2 - Entering an empty or malformed email address in the lost password form
    Given I am on the lost password form
    And I enter an empty or malformed email address <email>
    When I click the "Submit" button
    Then an error message tells me "Email address must be in the form ‘jane@doe.nz’"
    Examples:
      | email           |
      | ""              |
      | "user"          |
      | "user@"         |
      | "user@domain"   |
      | "user@domain."  |
      | "user@domain.c" |

  Scenario Outline: AC3 - Entering a valid email that is not known to the system in the lost password form
    Given I am on the lost password form
    And I enter a valid email that is not known to the system <email>
    When I click the "Submit" button
    Then a confirmation message tells me "An email was sent to the address if it was recognised"
    Examples:
      | email           |
      | "startup@user.com" |


  Scenario Outline: AC7 - Weak password error message
    Given I am on the reset password form
    When I enter the password <password>
    And I hit the save button
    Then an error message tells "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."
    Examples:
      | password |
      | "12345678" |
      | "12345678a" |
      | "user@gmail.com123456" |
      | "Test123456!" |
      | "User123456!@#" |
      | "passw0rd123!@#$^&*()" |