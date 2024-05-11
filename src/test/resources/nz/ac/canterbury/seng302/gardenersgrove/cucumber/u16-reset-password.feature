Feature: U16 As Sarah, I want to be able to change my password over email, so that I can still access my account
  even if I forget my password.

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