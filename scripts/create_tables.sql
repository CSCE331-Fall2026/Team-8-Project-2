CREATE TYPE order_status AS ENUM ('paid', 'canceled', 'refunded');
CREATE TYPE payment_method AS ENUM ('credit_card', 'debit_card', 'cash', 'gift_card', 'meal_pass');
CREATE TYPE storage_type AS ENUM ('freezer', 'refrigerator', 'pantry');

CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    order_status order_status DEFAULT 'paid',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    payment_method payment_method,
    order_total FLOAT
);

CREATE TABLE menu_items (
    menu_item_id INT PRIMARY KEY,
    menu_item_name VARCHAR(50),
    menu_price FLOAT
);

CREATE TABLE ingredients (
    ingredient_id INT PRIMARY KEY,
    ingredient_name VARCHAR(50),
    qty_in_stock FLOAT,
    storage_type storage_type,
    ingredient_cost FLOAT,
    expiry_date TIMESTAMPTZ,
    min_qty_on_hand FLOAT,
    measurement_units VARCHAR(20)
);

CREATE TABLE order_details (
    order_detail_id INT PRIMARY KEY,
    menu_item_id INT REFERENCES menu_items(menu_item_id),
    order_id INT REFERENCES orders(order_id),
    order_qty INT
);

CREATE TABLE menu_item_ingredients (
    menu_item_ingredient_id INT PRIMARY KEY,
    menu_item_id INT REFERENCES menu_items(menu_item_id),
    ingredient_id INT REFERENCES ingredients(ingredient_id),
    ingredient_qty FLOAT,
    premium_charge FLOAT
);