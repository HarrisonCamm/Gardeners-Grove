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
    Then an error message tells me <message>
    Examples:
      | email           | message         |
      | ""              | "Email address must be in the form ‘jane@doe.nz’" |
      | "user"          | "Email address must be in the form ‘jane@doe.nz’" |
      | "user@"         | "Email address must be in the form ‘jane@doe.nz’" |
      | "user@domain"   | "Email address must be in the form ‘jane@doe.nz’" |
      | "user@domain."  | "Email address must be in the form ‘jane@doe.nz’" |
      | "user@domain.c" | "Email address must be in the form ‘jane@doe.nz’" |

  Scenario Outline: AC3 - Entering a valid email that is not known to the system in the lost password form
    Given I am on the lost password form
    And I enter a valid email that is not known to the system <email>
    When I click the "Submit" button
    Then a confirmation message tells me "An email was sent to the address if it was recognised"
    Examples:
      | email           |
      | "startup@user.com" |
      | "testUser@gmail.com"|
      | "fakeEmail@asdf.com" |

  Scenario Outline: AC4 - Entering an email that is known to the system in the lost password form
    Given I am on the lost password form
    And I enter a valid email that is not known to the system <email>
    When I click the "Submit" button
    Then a confirmation message tells me "An email was sent to the address if it was recognised"
    And an email is sent to the email address with a link containing a unique reset token to update the password of the profile associated to that email
    Examples:
      | email           |
      | "startup@user.com" |
      | "testUser@gmail.com"|
      | "fakeEmail@asdf.com" |

  Scenario Outline: AC5 - Accessing the reset password form from the email link
    Given I received an email to reset my password using email <email>
    When I go to the given URL passed in the email
    Then I am asked to supply a new password with “new password” and “retype password” fields
    Examples:
      | email           |
      | "startup@user.com" |
      | "testUser@gmail.com"|
      | "fakeEmail@asdf.com" |

  Scenario Outline: AC6 - Entering two different passwords in the "new" and "retype password" fields
    Given I am on the reset password form
    And I enter two different passwords in “new” and “retype password” fields <password1> <password2>
    When I hit the save button
    Then an error message tells me <message>
    Examples:
      | password1 | password2 | message |
      | "Bob1!@#$" | "Bob1!@$" | "The new passwords do not match" |
      | "Alice123!" | "Alice321!" | "The new passwords do not match" |
      | "Password1!" | "Password2!" | "The new passwords do not match" |
      | "MySecret1!" | "MySecret2!" | "The new passwords do not match" |
      | "Test1234!" | "Test4321!" | "The new passwords do not match" |


  Scenario Outline: AC7 - Weak password error message
    Given I am on the reset password form
    When I enter the password <password>
    And I hit the save button
    Then an error message tells me <message>
    Examples:
      | password               | message |
      | "12345678"             | "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character." |
      | "12345678a"            | "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character." |
      | "user@gmail.com123456" | "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character." |
      | "Test123456!"          | "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character." |
      | "User123456!@#"        | "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character." |
      | "passw0rd123!@#$^&*()" | "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character." |

  Scenario Outline: AC8 - Entering fully compliant details in the reset password form
    Given I am on the reset password form
    When I enter fully compliant details with <password1> and <password2>
    Then my password is updated
    And an email is sent to my email address to confirm that my password has been updated
    And I am redirected to the login page
    Examples:
      | password1 | password2 |
      | "Bob1!@#$" | "Bob1!@#$" |
      | "Alice123!" | "Alice123!" |
      | "Password1!" | "Password1!" |
      | "V3eryS3cure___!" | "V3eryS3cure___!" |

  Scenario: AC9 - Reset password link expires after 10 minutes
    Given a reset password link was created
    When 10 minutes have passed since the link was created
    Then the reset token is deleted
    And it can’t be used to reset a password anymore


  Scenario: AC10 - User is redirected to the login page with an error message when clicking on an expired reset password link
    Given I click on a reset password link that has expired
    Then I am redirected to the login page
    And I see a message telling me "Reset password link has expired"