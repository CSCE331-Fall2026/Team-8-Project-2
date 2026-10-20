# SQL Optimization Report

## General Summary

A report on optimizing the SQL used to query the database in our project. In this report, the goal is to try and determine whether making use of indexes can improve query times. To do this, I will be using the SQL command used to query the Sales Report.

### Query Experimented on:
```
EXPLAIN ANALYZE
SELECT
    mi.menu_item_name,
    SUM(od.order_qty * cd.servings) AS quantity_sold,
    COALESCE(
        SUM(
            od.order_qty
            * cd.servings
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
  AND o.created_at >= TIMESTAMP '2026-09-19 00:00:00'
  AND o.created_at < TIMESTAMP '2026-10-20 00:00:00'
GROUP BY
    mi.menu_item_id,
    mi.menu_item_name
ORDER BY
    mi.menu_item_name;
```
*Note that small changes have been made to this command to make it easier to index and experiment on for this report.

### Index used:
The index I will be using is between the order_status attribute and the date. It was created using the following SQL command:
```
CREATE INDEX idx_orders_paid_created_at
ON orders (created_at)
WHERE order_status = 'paid';
```

## Experimentation

### Before:
The execution times taken to run the query before using indexes are: 5.187 ms, 6.985 ms, 5.682 ms

### After:
The execution times taken to run the query after using indexes are: 4.526 ms, 5.034 ms, 4.678 ms

### Observations:
There seems to be a difference between the two times, but it's rather small and a little inconsistent.

## Reflection

While the query time does seem to change, very little change was observed after indexing. It is almost negligible. One major reason for this could be that a majority of our orders entered into the database have the paid status, as most costumers do not refund or cancel their orders. Thus, despite the indexing marginally increasing search-through speeds, the effects are minimal.

Perhaps if we had used an index between two different values that had a larger representation in variety, then maybe indexes would be useful, but for this specific case, the effects are marginal at best.

## Conclusion

Although there is potential for optimization with indexes, we will not be using the index between order_status and date_created. The marginal improvements are not worth the extra storage space or larger update times, and we would rather just use standard look-ups.
