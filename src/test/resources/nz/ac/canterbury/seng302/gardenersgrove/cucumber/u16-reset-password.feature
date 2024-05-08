Feature: U16 As Sarah, I want to be able to change my password over email, so that I can still access my account
  even if I forget my password.

  Scenario: Weak password error message
    Given I am on the reset password form
    When I enter a weak password
    And I hit the save button
    Then an error message tells "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."