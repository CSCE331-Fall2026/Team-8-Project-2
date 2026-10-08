# Changelog

## Database Changelog

### Changes in response to TA feedback from 9/22 Lab

- add order_total to Orders entity: Minimizes query overhead by tracking total cost of an entire order in top level entity. Note that this only allows for whole order refunds in response to assignment scope from TA.

- add order_qty to OrderDetails entity: Reduces entity creation overhead by allowing for duplicate MenuItems to be contained in one entity. Also aids in cashier experience by allowing quantity to be a field in the UI to speed up the ordering process of duplicate items contained in the same order.

- add premium_charge to MenuItemIngredients: Allows for premium menu items to have an upcharge added to the base cost of a MenuItem such as a bowl.

- add measurement_units to Ingredients: Allows for greater clarity for how an item's stock is tracked.

- adjusted names of fields to tie them closer to their entity.
  - MenuItemIngredients: quantity -> ingredient_quantity
  - Orders: status -> order_status

### Changes in response to Database Population

While attempting to create test data, the group realized there was a missing step between orders and ingredients. MenuItems was too ambiguous and did not facilitate the creation of combo items like bowls and plates (what you actually pay for as a customer) and the creation of the entrees from individual ingredients like orange chicken.

- add 2 new tables: Combos and ComboDetails
  - Combos bridges the functional gap we saw in our previous schema and we now have 2 separate entities to track what a customer asks for and the recipes made by the employees.
  - ComboDetails is the join table that allows for the many-to-many relation to exist between Combos and MenuItems so that every permutation of combo does not need to be individually defined.

  - Combos:
    - combo_id (PK), combo_name, combo_price
  - ComboDetails:
    - combo_detail_id (PK), menu_item_id (FK), combo_id (FK), servings

  - Renamed MenuItemIngredients to MenuItemDetails to keep naming conventions consistent

### Changes in Response to GUI Implementation

#### Changes made in response to TA feedback in Phase 2
Changed code in data_generation to produce combos correctly as per TA feedback.

Cleaned up README.

#### Changes before end of Phase 3
Added views for all types of reports, inventory management, and cashier cart management
- Cashier view connected to database, properly reflecting prices and adding to database.
- Some Manager views connected to database, allowing for item discontinuation and price changes.

Combos table was moved to not be an inbetween step of the chain of OrderDetails -> X(Combos) -> ComboDetails. This change allows for ComboDetails to track the individual MenuItems without tying them to fixed Combo types.
- Code in data generation script has been changed according

menu_price was dropped as a field from MenuItems because price is dependent on the combo type and a premium charge, not the individual item.

Primary keys for id numbers are also now set to auto increment when they are created to simplify additions to database and data generation as a whole.

Add active field for MenuItems to track if the item has been discontinued or not without fully deleting the menu item.

Back button in cashier and manager views
