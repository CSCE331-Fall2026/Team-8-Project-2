/* From root project directory: psql -h csce-315-db.engr.tamu.edu -p 5432 -U team8 -d team8db -f ./scripts/create_tables.sql */

CREATE TYPE order_status AS ENUM ('paid', 'canceled', 'refunded');
CREATE TYPE payment_method AS ENUM ('credit_card', 'debit_card', 'cash', 'gift_card', 'meal_pass');
CREATE TYPE storage_type AS ENUM ('freezer', 'refrigerator', 'pantry');
CREATE TYPE combo_type AS ENUM ('Bowl', 'Plate', 'Big Plate', 'Family Meal', 'A la carte');

CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    order_status order_status DEFAULT 'paid',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    payment_method payment_method,
    order_total FLOAT
);

CREATE TABLE combos (
    combo_id INT PRIMARY KEY,
    combo_name combo_type,
    combo_price FLOAT
);

CREATE TABLE menu_items (
    menu_item_id INT PRIMARY KEY,
    menu_item_name VARCHAR(50),
    premium_charge FLOAT DEFAULT 0.0,
    menu_price FLOAT
);

CREATE TABLE ingredients (
    ingredient_id INT PRIMARY KEY,
    ingredient_name VARCHAR(50),
    quantity_in_stock FLOAT,
    storage_type storage_type,
    ingredient_cost FLOAT,
    expiry_date TIMESTAMPTZ,
    min_qty_on_hand FLOAT,
    measurement_units VARCHAR(20)
);

CREATE TABLE order_details (
    order_detail_id INT PRIMARY KEY,
    combo_id INT REFERENCES combos(combo_id),
    order_id INT REFERENCES orders(order_id),
    order_qty INT
);

CREATE TABLE combo_details (
    combo_detail_id INT PRIMARY KEY,
    menu_item_id INT REFERENCES menu_items(menu_item_id),
    combo_id INT REFERENCES combos(combo_id),
    servings FLOAT
);

CREATE TABLE menu_item_details (
    menu_item_detail_id INT PRIMARY KEY,
    menu_item_id INT REFERENCES menu_items(menu_item_id),
    ingredient_id INT REFERENCES ingredients(ingredient_id),
    ingredient_quantity FLOAT
);