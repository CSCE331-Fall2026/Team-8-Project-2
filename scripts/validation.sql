/* From root project directory: psql -h csce-315-db.engr.tamu.edu -p 5432 -U team8 -d team8db -f ./scripts/validation.sql */

-- Q1: 52 Weeks of sales data

\echo '\nQ1: Checking order count per week...'
SELECT DATE_TRUNC('week', created_at) AS week_start, COUNT(*) AS order_count
FROM orders
GROUP BY DATE_TRUNC('week', created_at)
ORDER BY week_start;
\echo 'Q1 end'

-----------------------------------------------

-- Q2: Orders by hour
\echo '\nQ2: Check order quantity and totals by hour...'
SELECT 
    EXTRACT(HOUR FROM o.created_at)::INT AS hour_of_day,
    COUNT(*) AS total_orders,
    ROUND(SUM(o.order_total)::NUMERIC, 2) AS total_sales
FROM orders AS o
GROUP BY EXTRACT(HOUR FROM o.created_at)
ORDER BY hour_of_day;

\echo 'Q2 end'

-----------------------------------------------

-- Q3: 2 highest sales days

\echo '\nQ3: Checking 2 highest sales days...'
SELECT
    created_at::date AS day,
    COUNT(*) AS order_count,
    ROUND(SUM(order_total)::NUMERIC, 2) AS total_sold
FROM orders
GROUP BY created_at::date
ORDER BY total_sold DESC
LIMIT 2;
\echo 'Q3 end'

-----------------------------------------------

-- Q4: Inventory items for 20 menu items
\echo '\nQ4: Checking inventory items for 20 items...'
SELECT 
    mi.menu_item_id, 
    mi.menu_item_name, 
    COUNT(i.ingredient_id) AS ingredient_count
FROM menu_items AS mi
JOIN menu_item_details AS mid ON mi.menu_item_id = mid.menu_item_id
JOIN ingredients AS i ON i.ingredient_id = mid.ingredient_id
GROUP BY mi.menu_item_id, mi.menu_item_name
ORDER BY mi.menu_item_id ASC
LIMIT 20;
\echo 'Q4 end'

-----------------------------------------------

-- Q5: best of the worst

\echo '\nQ5: Checking best selling item on worst sales day...'
WITH worst_day AS (
    SELECT DATE_TRUNC('day', created_at) AS day_start, COUNT(*) AS order_count
    FROM orders
    GROUP BY DATE_TRUNC('day', created_at)
    ORDER BY order_count ASC
    LIMIT 1
)
SELECT
    mi.menu_item_id,
    mi.menu_item_name,
    SUM(od.order_qty) AS sold_total
FROM orders o
JOIN worst_day wd ON DATE_TRUNC('day', o.created_at) = wd.day_start
JOIN order_details od ON od.order_id = o.order_id
JOIN combos AS c ON c.combo_id = od.combo_id
JOIN combo_details AS cd on c.combo_id = cd.combo_id
JOIN menu_items AS mi ON mi.menu_item_id = cd.menu_item_id
GROUP BY mi.menu_item_id, mi.menu_item_name
ORDER BY sold_total DESC
LIMIT 1;
\echo 'Q5 end'

-----------------------------------------------

-- Q6: Check ingredients below restock level

\echo '\nQ6: Checking which items need to be restocked...'
SELECT i.ingredient_id, i.ingredient_name, i.quantity_in_stock, i.min_qty_on_hand, i.measurement_units
FROM ingredients AS i
WHERE i.quantity_in_stock < i.min_qty_on_hand;

\echo 'Q6 end'

-----------------------------------------------

-- Q7: Orders are only after 10am, before 9pm, and weekdays only

ALTER TABLE orders DROP CONSTRAINT IF EXISTS ck_operating_hours;

\echo '\nQ7: Checking orders between valid times...'
ALTER TABLE orders
ADD CONSTRAINT ck_operating_hours
CHECK (
    CAST(created_at AS time) >= '10:00:00'
    AND CAST(created_at AS time) <= '21:00:00'
    AND EXTRACT(ISODOW FROM created_at) < 6
);

\echo 'Q7 end'

-----------------------------------------------

-- Q8: Breakdown of Menu Items by storage type of ingredients

\echo '\nQ8: Checking how many storage type ingredients go into each menu item...'
SELECT m.menu_item_id, m.menu_item_name, 
    COUNT(*) FILTER (WHERE i.storage_type = 'pantry') AS pantry_ingredients,
    COUNT(*) FILTER (WHERE i.storage_type = 'refrigerator') AS refrigerator_ingredients,
    COUNT(*) FILTER (WHERE i.storage_type = 'freezer') AS freezer_ingredients
FROM menu_items AS m
JOIN menu_item_details AS md ON md.menu_item_id = m.menu_item_id
JOIN ingredients as i ON i.ingredient_id = md.ingredient_id
GROUP BY m.menu_item_id, m.menu_item_name
ORDER BY m.menu_item_id ASC;


\echo 'Q8 end'

-----------------------------------------------

-- Q9: 

\echo '\nQ9: Checking all menu items that have rice...'
SELECT m.menu_item_id, m.menu_item_name FROM menu_items AS m
JOIN menu_item_details AS md ON m.menu_item_id = md.menu_item_id
JOIN ingredients AS i ON i.ingredient_id = md.ingredient_id
WHERE i.ingredient_name = 'Rice';

-----------------------------------------------