# SQL Optimization Report

## General Summary

A report on optimizing the SQL used to query the database in our project. In this report, the goal is to try and determine whether making use of indexes can improve query times. To do this, I will be using the SQL command used to query the Sales Report.

The exact query is the following:
```
EXPLAIN ANALYZE
SELECT
    mi.menu_item_name,
    SUM(od.order_qty * cd.servings) AS quantity_sold,
    COALESCE(
        SUM(
            od.order_qty * cd.servings
            * COALESCE(mi.premium_charge, 0)
        ),
        0
    ) AS premium_revenue
FROM orders o
JOIN order_details od
    ON od.order_id = o.order_id
JOIN combo_details cd
    ON cd.order_detail_id = od.order_detail_id
JOIN menu_items mi
    ON mi.menu_item_id = cd.menu_item_id
WHERE o.order_status = 'paid'
  AND o.created_at::date BETWEEN DATE '2026-09-19'
                              AND DATE '2026-10-19'
GROUP BY mi.menu_item_id, mi.menu_item_name
ORDER BY mi.menu_item_name;
```

