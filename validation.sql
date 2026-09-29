ALTER TABLE orders
ADD CONSTRAINT CK_operating_hours
CHECK (
    CAST(created_at AS time) >= '10:00:00'
    AND CAST(created_at AS time) <= '21:00:00'
    AND EXTRACT(ISODOW FROM created_at) < 6
);
-- after 10am before 9pm and weekdays only


SELECT
    created_at::date AS day,
    COUNT(*) AS row_count
FROM orders
GROUP BY created_at::date
ORDER BY row_count ASC
LIMIT 1;

-- lowest sales day

SELECT
    created_at::date AS day,
    COUNT(*) AS row_count
FROM orders
GROUP BY created_at::date
ORDER BY row_count DESC
LIMIT 1;

-- highet sales day



-- Top 10 Days by Sales
SELECT created_at::date AS day,
       SUM(order_total) AS total_sales
FROM orders
GROUP BY day
ORDER BY total_sales DESC
LIMIT 10;


-- 20 Valid Menu Items
SELECT COUNT(*) AS makeable_menu_items,
       COUNT(*) >= 20 AS passes
FROM (                     
    SELECT mi.menu_item_id
    FROM menu_item_ingredients mi
    JOIN ingredients i ON mi.ingredient_id = i.ingredient_id
    GROUP BY mi.menu_item_id
    HAVING MIN(i.qty_in_stock) > 0
) makeable;