# Team-8-Project-2
Front-End:
- Arnav
- Will

Backend
- Viswanath
- Tyler

Database
- Brandon

# Database Schema and Design
## Orders
- id: To keep track of orders uniquely.
- status: Is it refunded, cancelled, or paid? 
- created_at: What date and time was the order created?
- updated_at: What date and time was the order updated (i.e. refund), if at all?
- total amount: The total amount of money paid for the order, used to track performance and profit.
- payment_method: How was the order paid for?
  
For the manager, this is meant to keep track of the items sold through the day, with various metrics to help understand the order. By knowing how much was spent, they can calculate profit, and by knowing what method was used for payment, they can make policies targeting specific payment methods. Knowing how many items are used in a specific order can also help them keep a mental note of what items are used often.

For the cashier, this is meant to help them keep track of each item, when it was bought, what was in the order, and similar things.

## OrderDetails
- id: To keep track of order details uniquely.
- menu item: To keep track of what menu items went into the order.
- order number: To keep track of the order to which the order details belong to.

This is used as a join table to connect many menu items into a single order.

## MenuItem
- id: To keep track of each menu item uniquely.
- name: To keep track of the menu item name.
- ingredients: To keep track of the ingredients used to make the menu item.
- cost: The cost of the item the customer needs to pay.

For the manager, keeping track of what ingredients are used in a specific order can help monitor stock, and what items are being sold. The cost will also help monitor gains, and through calculations, profits.

For the cashier, by keeping track of the name of the menu item, it becomes easy to to see what items have been registered already. This can help if items need to be deleted, refunded, or cancelled. 

## Ingredients
- id: To keep track of each ingredient uniquely.
- name: To help humans identify which item each item is.
- quantity_in_stock: The amount of any given ingredient currently in stock.
- storage_type: Where is the item stored? Pantry, refrigerator, or freezer?
- cost: The cost to buy a serving of the given ingredient.
- expiry date: The day any given shipment of an item is expected to expire.

For the manager, knowing the quantity in stock, as well as the storage type of any given item can help in managing an item

For the cashier, by knowing the names of ingredients, 
