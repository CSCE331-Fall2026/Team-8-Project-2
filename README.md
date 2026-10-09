# Team-8-Project-2

Front-End:

- Arnav
- Will

Backend

- Viswanath
- Tyler

PM

- Brandon

# Running the GUI

The JavaFX POS app lives in [`src/`](src/). You need a recent JDK (**23+**, recommended **25 or 27**), the OpenJFX SDK, and the PostgreSQL JDBC driver.

## 1. Set up files

Download the Linux/macOS/Windows JavaFX **SDK** zip from [openjfx.io](https://openjfx.io/) (match your OS) and the PostgreSQL JDBC driver [`postgresql-42.2.8.jar`](https://jdbc.postgresql.org/download/). Put them in the **project root**, and create `src/dbSetup.java` with the team database credentials:

```text
Team-8-Project-2/
  javafx-sdk-25/
    lib/
  postgresql-42.2.8.jar
  src/
    dbSetup.java
```

```java
public final class dbSetup {
  public static final String user = "<db username>";
  public static final String pswd = "<db password>";
}
```

`javafx-sdk-25/`, `javafx-sdk-27/`, `*.jar`, and `dbSetup.java` are gitignored, so each person has to add them locally.

Confirm `javac -version` is 23+ (not an older 21-only toolchain). On Fedora, if `java` is new but `javac` is 21:

```bash
export JAVA_HOME=/usr/lib/jvm/java-27   # or java-25 / java-latest-openjdk
export PATH="$JAVA_HOME/bin:$PATH"
javac -version
```

## 2. Compile and run

From `src/`:

Linux / macOS

```bash
cd src
javac --module-path ../javafx-sdk-27/lib --add-modules javafx.controls,javafx.fxml -cp ".:postgresql-42.2.8.jar" *.java
java --enable-native-access=javafx.graphics --module-path ../javafx-sdk-27/lib --add-modules  javafx.controls,javafx.fxml -cp ".:postgresql-42.2.8.jar" GUIApp
```

Windows

```bash
cd src
javac --module-path ../javafx-sdk-25/lib --add-modules javafx.controls,javafx.fxml *.java
java --enable-native-access=javafx.graphics --module-path ../javafx-sdk-25/lib --add-modules javafx.controls,javafx.fxml -cp ".;../postgresql-42.2.8.jar" GUIApp
```

`-cp ".:../postgresql-42.2.8.jar"` loads the compiled classes in `src/` plus the Postgres driver. Without it, cart prices and placing orders fail with `No suitable driver found`. On Windows, use `;` instead of `:` in the classpath.

If your folder is `javafx-sdk-27`, swap that name in both commands. If the SDK lives somewhere else (for example one level above the project), change `../javafx-sdk-25/lib` to that path.

You need to be on the TAMU network or VPN to reach the database.

Then open **Cashier** from the home screen to build bowls/plates into the cart. Prices are pulled from the `combos` table each time the cart refreshes.

More detail: [`src/README.md`](src/README.md).

# Database Schema and Design

No AI was used to critique our database.

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

## Combos

- combo_id: Tracks an individual combo that is on the menu
- combo_name: Hardcoded names of potential order types
- combo_price: How much this combo variation costs

This table holds the fixed info for combos.

This table is the first thing an employee interacts with when ringing up an order for a customer.

The manager will be able to track what types of orders people are most commonly making.

## OrderDetails

- order_detail_id: Tracks an individual product contained in a larger order.
- combo_id (FK): Foreign key that connects to the highest level product entity for items such as bowls, plates, etc. that hold entrees and sides.
- order_id (FK): To keep track of the order to which the order details entity belongs to.
- order_qty: Tracks how many of a specific MenuItem are in an Order

When placing an order at the register, if 2 bowls are ordered they will belong to order #x. OrderDetails allows for all of the bowls (or other combos) that are added to an order to be tracked individually. If the bowls are identical, the quantity can be changed.

For the cashier, tracking the order quantity allows for the count of a specific menu item to simply be duplicated instead of requiring the same order to be rung up twice. This will help to reduce the time a customer spends at a register.

## ComboDetails

- combo_detail_id: Tracks an individual item being added to an OrderDetail entity
- menu_item_id: Tracks which entree/side is being added to a given combo
- order_detail_id: Tracks which order detail a menu item is being assigned to
- servings: How much of a menu item is being added in this detail

This table will hold the menu items that are inside a given combo. Because it exists, a cashier will not have to scroll down a large list looking for the exact order permuation that the customer asks for.

## MenuItem

- menu_item_id: To keep track of each menu item uniquely such as bowls, plates, and combos.
- menu_item_name: To keep track of the menu item name.
- premium_charge: For applicable items, adds a flat charge on top of the base price. This column will be used more on the business logic of calculating order price.
- active: Tracks if the item is discontinued or active. Having this field avoids deletions to keep order history intact.
- menu_item_type: tracks what kind of category this menu item belongs to like an entree, side, drink, or appetizer

This table holds the names and ids of full items that can be on a menu like orange chicken. It also tracks if it has a premium charge.

For the manager, this will allow them to track what items on the menu are performing well and are the most popular.

For the cashier, this entity exists to be able to ring up an order with multiple different ingredients within it through the use of the MenuItemDetails join table.

## MenuItemDetails

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

## Style Guide

### Python

- Snake case and capitalized global variables
- Key value pairs on separate lines when block defined
- Two lines between every function, one between variables
- Global variables all stay at the top
- Four spaces of indent to divide groups (i.e. if statements, while statements, functions)

### Java

- Two space indent when using a new group (i.e. if statements, while statements, functions)
- One line between every function
- Comments in important spots or inline comments only.

### SQL

- All lower case and snake case for fields and entity names
- Line break, indentation, and trailing comma for select statements with > 2 values

### fxml

- Two space indent before in every group (i.e. <menus>, <items>)
- No lines of space between lines of code.
- One line of space between imports, as well as another line for fxml version

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

If you want, I can also add a short comment explaining the meaning of the MENU_INGREDIENT_MAP entries and the generated SQL columns.

- Fix the logic error in this function
  the generate_order_details function is currently assigning a single random item to each order, but the function is intended to generate details for each item in the order. As written, the loop only creates one order_detail row per order and doesn’t properly track the total across multiple items. I’d change it to iterate through a selected set of menu items for each order, add one row per item with its quantity, and update order_total by summing the price of each selected item. This will ensure the order_details table reflects the actual content of each order and keeps totals consistent.
- Repeat the script append for each menu item.
  I repeated this script pattern for each menu item so the data generator appends a generated record for every item in the menu. Each menu item is processed in the same way, adding its associated ingredients, pricing, and generated SQL insert values to the output script.

## AI Chat Log (Oct 2026)

Cursor AI coding session used to get the JDBC demo running, set up JavaFX, organize `src/`, and create the ReadMe. Summary of the full chat below.

### JDBC Swing demo (`demo/jdbc_swing_demo`)

**User:** Showed terminal error `ClassNotFoundException: jdbcSQL` after  
`java -cp ".;postgresql-42.2.8.jar" jdbcSQL`.

**AI:** On Linux the classpath separator is `:`, not `;` (Windows). Use:

```bash
java -cp ".:postgresql-42.2.8.jar" jdbcSQL
```

**User:** Then got `No suitable driver found for jdbc:postgresql://...`.

**AI:** The PostgreSQL JAR was missing from the folder. Download it into the demo directory, then run with the Linux classpath again.

### Organize `src/`

**User:** “organize src little”

**AI:** Moved all `.fxml` files into `src/fxml/` and updated `GUIApp` to load `fxml/GUI.fxml`. Java sources stayed at `src/` root so compile/run commands stay simple.

### FlowPane type buttons

**User:** Buttons side by side until overflow, then wrap.

**AI:** Put type buttons in a `FlowPane` with `hgap` / `vgap`.

### README run instructions

**User:** Add how to get this running to the README.

**AI:** Added **Running the GUI** at the top of this file and rewrote [`src/README.md`](src/README.md) with compile/run steps and troubleshooting.

### Prompt used for this log

- Add this entire chat to the readme

### Prompt used to reformat Cashier.fxml

Don't change a LINE of code, but can you change the style of the code to match the following:

FXML

- Two space indent before in every group (i.e. <menus>, <items>)
- No lines of space between lines of code.

\*Also has Cashier.fxml's code pasted here. The output was just the reformatted Cashier.fxml
