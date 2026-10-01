# Team-8-Project-2

Front-End:

- Arnav
- Will

Backend

- Viswanath
- Tyler

PM

Will Wrote stuff here

- Brandon

# Lab Day 9/24

Arnav Karekar Lab Merge - another change
Brandon banch merge :D

Tyler's branch merge

# Database Schema and Design

No AI was used to critique our database.

Other change that will made

## Orders

- order_id: To keep track of orders uniquely.
- order_status: Is it refunded, cancelled, or paid?
- created_at: What date and time was the order created?
- updated_at: What date and time was the order updated (i.e. refund), if at all?
- payment_method: How was the order paid for?
- order_total: How much did the entire order cost?

As the highest level in the database, many relevant data points related to a specific order are found through deeper queries to avoid redundancy. For example, finding the total amount for a specific order is done by checking the OrderDetails entities with the same order_id and then summed with the menu_price of the individual items in the order.

For the manager, this is meant to keep track of the items sold through the day, with various metrics to help understand the order. By knowing how much was spent, they can calculate profit, and by knowing what method was used for payment, they can make policies targeting specific payment methods. Knowing how many items are used in a specific order can also help them keep a mental note of what items are used often. By tracking the order total here, the history of previous orders and sales reports are unaffected by changes in menu prices.

For the cashier, this is meant to help them keep track of each item, when it was bought, what was in the order, and similar things.

## OrderDetails

- order_detail_id: Tracks an individual product contained in a larger order.
- combo_id (FK): Foreign key that connects to the highest level product entity for items such as bowls, plates, etc. that hold entrees and sides.
- order_id (FK): To keep track of the order to which the order details entity belongs to.
- order_qty: Tracks how many of a specific MenuItem are in an Order

In order to facilitate the many-to-many relation between many orders containing many combo items, this join table exists to allow for orders to contain multiple different items from the menu.

For the cashier, tracking the order quantity allows for the count of a specific menu item to simply be duplicated instead of requiring the same order to be rung up twice. This will help to reduce the time a customer spends at a register.

## Combos

- combo_id: Tracks an individual combo being ordered
- combo_name: Hardcoded names of potential order types
- combo_price: How much this combo variation costs

This table is the first thing an employee interacts with when ringing up an order for a customer.

The manager will be able to track what types of orders people are most commonly making.

## ComboDetails

- combo_detail_id: Tracks an individual item being added to a combo
- menu_item_id: Tracks which entree/side is being added to a given combo
- combo_id: Tracks which combo a menu item is being assigned to
- servings: How much of a menu item is being added in this detail

This table allows for a many-to-many relation to exist between combos and menu items. Because it exists, a cashier will not have to scroll down a large list looking for the exact order permuation that the customer asks for.

## MenuItem

- menu_item_id: To keep track of each menu item uniquely such as bowls, plates, and combos.
- menu_item_name: To keep track of the menu item name.
- premium_charge: For applicable items, adds a flat charge on top of the base price. This column will be used more on the business logic of calculating order price.
- menu_price: The cost of the item the customer needs to pay if it is purchased a la carte.

For the manager, this will allow them to track what items on the menu are performing well and are the most popular. In addition, it will allow for them to calculate the profit for each item by comparing the menu_price to the sum of the ingredient costs for the menu item.

For the cashier, this entity exists to be able to ring up an order with multiple different ingredients within it through the use of the MenuItemIngredients join table.

## MenuItemIngredients

- menu_item_ingredient_id: Tracks the action of an ingredient being placed inside of a menu item. For example the orange chicken going into a bowl.
- menu_item_id (FK): Tracks the individual menu item that an ingredient is becoming a part of. In the previous example this would be the bowl the chicken is going into.
- ingredient_id (FK): Tracks the ingredient, which in this case would be the chicken.
- ingredient_quantity: Allows for multiple servings of a single ingredient in one menu item.

This is another join table that bridges the many-to-many relation of many menu items containing many ingredients. This functionally allows for the cashier to provide mixed ingredient bowls and the quantity field helps a manager track how much of each ingredient is being sold and for the system to update the stock accordingly.

## Ingredients

- ingredient_id: To keep track of each ingredient uniquely.
- ingredient_name: To help humans identify which item each item is.
- quantity_in_stock: The amount of any given ingredient currently in stock.
- storage_type: Where is the item stored? Pantry, refrigerator, or freezer?
- ingredient_cost: The cost to buy a serving of the given ingredient.
- expiry_date: The day any given item is expected to expire.
- min_qty_on_hand: The minimum amount of stock for an ingredient before it should be restocked.
- measurement_units: Units of measurement that the item is tracked in (servings, pounds, ...)

For the manager, knowing the quantity in stock, as well as the storage type of any given item can help in managing an item. The system can also allow for the manager to be notified of products that need to be restocked.

For the cashier, the ingredients need to be accessed in order to add them into a menu item via the join table.

## Changelog

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

## Style Guide

### Python

- Snake case and capitalized global variables
- Key value pairs on separate lines when block defined

### SQL

- All lower case and snake case for fields and entity names
- Line break, indentation, and trailing comma for select statements with > 2 values

## AI Usage

- Used to populate the the map of the menu items, ingredients, with numbers.
- Prompts: Go to the official panda express menu and get only the real items on the menu, then populate this map with the data of the official panda express.
  I used the official Panda Express menu as the source and populated the maps with only real menu items, excluding any non-official or placeholder entries. I kept the set to actual Panda Express dishes, sides, appetizers, and a single generic soda item, then mapped those items to realistic ingredients and prices for the generated data.
- Add comments so that the variables are understandable.
  ✅ Added explanatory comments
  I added comments above the main data maps so the numeric tuple structure is clearer:

MENU_ITEMS: (menu_item_id, menu_item_name, menu_price)
INGREDIENTS: (ingredient_id, ingredient_name, qty_in_stock, storage_type, ingredient_cost, measurement_units)
This makes the script easier to follow without needing to infer each field from the list order.

- Fix the logic error in this function
  the generate_order_details function is currently assigning a single random item to each order, but the function is intended to generate details for each item in the order. As written, the loop only creates one order_detail row per order and doesn’t properly track the total across multiple items. I’d change it to iterate through a selected set of menu items for each order, add one row per item with its quantity, and update order_total by summing the price of each selected item. This will ensure the order_details table reflects the actual content of each order and keeps totals consistent.
- Repeat the script append for each menu item.
  I repeated this script pattern for each menu item so the data generator appends a generated record for every item in the menu. Each menu item is processed in the same way, adding its associated ingredients, pricing, and generated SQL insert values to the output script.
