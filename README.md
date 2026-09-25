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
- menu_item (FK): Foreign key that connects to the highest level product entity for items such as bowls, plates, combos, etc. that hold ingredients.
- order_id (FK): To keep track of the order to which the order details entity belongs to.
- order_qty: Tracks how many of a specific MenuItem are in an Order

In order to facilitate the many-to-many relation between many orders containing many menu items, this join table exists to allow for orders to contain multiple different items from the menu.

For the cashier, tracking the order quantity allows for the count of a specific menu item to simply be duplicated instead of requiring the same order to be rung up twice. This will help to reduce the time a customer spends at a register.

## MenuItem

- menu_item_id: To keep track of each menu item uniquely such as bowls, plates, and combos.
- menu_item_name: To keep track of the menu item name.
- menu_price: The cost of the item the customer needs to pay.

For the manager, this will allow them to track what items on the menu are performing well and are the most popular. In addition, it will allow for them to calculate the profit for each item by comparing the menu_price to the sum of the ingredient costs for the menu item.

For the cashier, this entity exists to be able to ring up an order with multiple different ingredients within it through the use of the MenuItemIngredients join table.

## MenuItemIngredients

- menu_item_ingredient_id: Tracks the action of an ingredient being placed inside of a menu item. For example the orange chicken going into a bowl.
- menu_item_id (FK): Tracks the individual menu item that an ingredient is becoming a part of. In the previous example this would be the bowl the chicken is going into.
- ingredient_id (FK): Tracks the ingredient, which in this case would be the chicken.
- ingredient_quantity: Allows for multiple servings of a single ingredient in one menu item.
- premium_charge: Allows for an upcharge to be added for premium products.

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
