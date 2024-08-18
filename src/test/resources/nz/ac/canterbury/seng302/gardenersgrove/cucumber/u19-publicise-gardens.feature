Feature: U19 - Publicise Garden
  As Inaya, I want to be able to make my garden public so that others can see what I’m growing.

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"
    And I am on the garden details page for a garden I own

    Scenario: AC1 - Garden is made public
      Given I am on the garden details page
      When I mark a checkbox labelled "Make my garden public"
      Then my garden will be visible in search results

    Scenario: AC2 - Creating a garden with a valid description
      Given I am on the create garden form
      And I enter a description with description length
      When I click the submit button on the create garden form
      Then A new garden is created on my account


      Scenario: AC5 - Removing description from garden
        Given I am on the edit garden page for a garden that I own
        When I delete the existing description
        And I click the submit button on the edit garden form
        Then The description is removed