Connect to database via psql commandline
 - Must use vpn if not on campus
 - Use a terminal, Putty or similar for PC.

Useful psql commands:
  \l  : List all databases on the server.
  \c <database_name>  : Connect / switch to a different database.
  \conninfo  : Display information about the current database connection.
  \du  : List all users and their assigned roles/privileges.
  \q  : Quit / exit the psql shell.
  
  \dt  : List all tables in the current database.
  \d <table_name>  : Describe a specific table (shows columns, data types, indexes).
  \dv  : List all views.

Connect to database:  // -h is AWS; -U is user name; -d is database name
  psql -h csce-315-db.engr.tamu.edu -U admin_shawna -d hardware_store_sthomas

Show no tables and exit:
  \dt
  \q

Populate database:
  Show create_sales.sql
  psql -h csce-315-db.engr.tamu.edu -U admin_shawna -d hardware_store_sthomas -f create_sales.sql

Show contents:
  psql -h csce-315-db.engr.tamu.edu -U admin_shawna -d hardware_store_sthomas
  \dt

  // show basic selection
  \d product
  SELECT p_code, p_descript, v_code, p_qoh, p_min FROM product;

  // show selection where quantity is low
  SELECT p_code, p_descript, v_code, p_qoh, p_min FROM product WHERE p_qoh <= p_min;
  SELECT p_code, p_descript, v_code, p_qoh, p_min FROM product WHERE p_qoh <= 2*p_min;

  // show basic computation: totaling inventory value
  SELECT p_code, p_descript, v_code, p_qoh, p_price, p_qoh * p_price AS inventory_value FROM product;
  SELECT SUM(p_qoh * p_price) AS total_inventory_value FROM product;

  // show subquery: selecting items above the average price
  SELECT AVG(p_price) AS average_price FROM product;
  SELECT p_code, p_descript, p_price FROM product WHERE p_price > (SELECT AVG(p_price) FROM product);

  // show join on vendor
  \d vendor
  SELECT p_code, p_descript, v_code, p_qoh, p_min FROM product;
  SELECT p.p_code, p.p_descript, v.v_name, p.p_qoh, p.p_min FROM product p JOIN vendor v ON p.v_code = v.v_code ORDER BY v.v_name;
    // above line ignores null rows (no match), show left join
  SELECT p.p_code, p.p_descript, v.v_name, p.p_qoh, p.p_min FROM product p LEFT JOIN vendor v ON p.v_code = v.v_code ORDER BY v.v_name;

  // show aggregation: number of products and average price per vendor
  SELECT v.v_code, v.v_name, COUNT(*) AS num_products, AVG(p.p_price) AS avg_price FROM product p LEFT JOIN vendor v ON p.v_code = v.v_code GROUP BY v.v_code, v.v_name ORDER BY v.v_name;
    // above line has null vendors which don't makes sense, go back to join
  SELECT v.v_code, v.v_name, COUNT(*) AS num_products, AVG(p.p_price) AS avg_price FROM product p JOIN vendor v ON p.v_code = v.v_code GROUP BY v.v_code, v.v_name ORDER BY v.v_name;
    // next show order by product num
  SELECT v.v_code, v.v_name, COUNT(*) AS num_products, AVG(p.p_price) AS avg_price FROM product p JOIN vendor v ON p.v_code = v.v_code GROUP BY v.v_code, v.v_name ORDER BY num_products DESC;
    // next show only those with multiple products
  SELECT v.v_code, v.v_name, COUNT(*) AS num_products, AVG(p.p_price) AS avg_price FROM product p JOIN vendor v ON p.v_code = v.v_code GROUP BY v.v_code, v.v_name HAVING COUNT(*) > 1 ORDER BY num_products DESC;

  // show join with subqueries: show products whose price is higher than the vendor average
  SELECT p.p_code, p.p_descript, v.v_name, p.p_price FROM product p JOIN vendor v ON p.v_code = v.v_code ORDER BY v.v_name;
  SELECT v.v_code, v.v_name, AVG(p.p_price) AS avg_price FROM product p JOIN vendor v ON p.v_code = v.v_code GROUP BY v.v_code, v.v_name ORDER BY v.v_name;
  SELECT p.p_code, p.p_descript, v.v_name, p.p_price FROM product p JOIN vendor v ON p.v_code = v.v_code WHERE p.p_price > (SELECT AVG(p2.p_price) FROM product p2 WHERE p2.v_code = p.v_code) ORDER BY v.v_name;

  // show a view: vendor average price so don't have to recompute the join
  CREATE VIEW vendor_avg_price AS SELECT v.v_code, v.v_name, AVG(p.p_price) AS avg_price FROM product p JOIN vendor v ON p.v_code = v.v_code GROUP BY v.v_code, v.v_name;
  SELECT * FROM vendor_avg_price ORDER BY v_name;
  SELECT p.p_code, p.p_descript, v.v_name, p.p_price, va.avg_price FROM product p JOIN vendor v ON p.v_code = v.v_code JOIN vendor_avg_price va ON va.v_code = v.v_code WHERE p.p_price > va.avg_price ORDER BY v.v_name;

Clear database:
  Show drop_sales.sql
  psql -h csce-315-db.engr.tamu.edu -U admin_shawna -d hardware_store_sthomas -f drop_sales.sql

Show cleared:
  psql -h csce-315-db.engr.tamu.edu -U admin_shawna -d hardware_store_sthomas
  \dt
  \q

