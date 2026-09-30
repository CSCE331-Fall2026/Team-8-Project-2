/* From root project directory: psql -h csce-315-db.engr.tamu.edu -p 5432 -U team8 -d team8db -f ./scripts/drop_tables.sql */

DROP TABLE IF EXISTS "order_details" CASCADE;
DROP TABLE IF EXISTS "menu_item_details" CASCADE;
DROP TABLE IF EXISTS "combo_details" CASCADE;
DROP TABLE IF EXISTS "orders" CASCADE;
DROP TABLE IF EXISTS "combos" CASCADE;
DROP TABLE IF EXISTS "menu_items" CASCADE;
DROP TABLE IF EXISTS "ingredients" CASCADE;

DROP TYPE IF EXISTS "order_status" CASCADE;
DROP TYPE IF EXISTS "payment_method" CASCADE;
DROP TYPE IF EXISTS "storage_type" CASCADE;
DROP TYPE IF EXISTS "combo_type" CASCADE;