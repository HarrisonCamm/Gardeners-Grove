Feature: U6007 - Unlockable content
  As Inaya, I want to unlock new content and collectable items for my application, so that I can customize my app to my
  liking and show off my commitment to Gardener's Grove and its users.

  Background: User is logged in
    Given I am logged in with email "inaya@email.com" and password "Password1!"

  Scenario: AC1 - View Inventory
    Given I am anywhere in the system
    When I click Inventory
    Then I am shown my inventory of items

  Scenario: AC2 - View Shop
    Given I am anywhere in the system
    When I click Shop
    Then I am shown the shop

  Scenario: AC3 - View Items For Sale
    Given I am in the shop
    Then I can see a list of items for sale with a picture, name and price in Blooms

  Scenario: AC4 - View Items in Inventory
    Given I am in my inventory
    Then I can see a list of my items I have purchased that have a picture, name and quantity

  Scenario: AC5 - Insufficient Balance Message
    Given I am in the shop
    When I attempt to buy an item costing more than my current Blooms balance
    Then I am shown the error message "Insufficient Bloom balance"
    And the item is not added to my items

  Scenario: AC6 - Successful Purchase
    Given I am in the shop
    When I buy an item costing less than or equal to my current Blooms balance
    Then that item is added to my inventory
    And the items cost in Blooms is deducted from my account
    And I am shown a confirmation message "Purchase successful"

  Scenario: AC7 - Quantity of Item Displayed
    Given I have more than one of the same item
    When I view my inventory
    Then the quantity is displayed alongside the item rather than displaying multiple instances of the item

  Scenario: AC8 - Item details
    Given I view my inventory
    When I click on an item
    Then I am taken to a page for that item which displays more information on the item including picture, name, description, original price, and resale price

  Scenario: AC9 - Sell Button
    Given I am viewing an item in my inventory
    When I click the Sell button for that item
    Then a confirmation popup with a cancel button and confirm button is shown with the message "Sell item back to shop for {amount}?"

  Scenario: AC10 - Transaction History
    Given I purchase an item
    When I check my Bloom transaction history
    Then I see an entry detailing the date, time, the name of the item and the items sell price with a negative sign


