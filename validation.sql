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


