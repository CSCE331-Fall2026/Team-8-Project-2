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

-- highest sales day

SELECT DATE_TRUNC('week', created_at) AS week_start, COUNT(*) AS order_count
FROM orders
GROUP BY DATE_TRUNC('week', created_at)
ORDER BY week_start;

-- order count per week

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
JOIN orderdetails od ON od.order_id = o.order_id
JOIN menuitem mi ON mi.menu_item_id = od.menu_item
GROUP BY mi.menu_item_id, mi.menu_item_name
ORDER BY sold_total DESC
LIMIT 1;

-- best of the worst

