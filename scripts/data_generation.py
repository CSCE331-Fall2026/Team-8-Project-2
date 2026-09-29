import random
from datetime import datetime, timedelta
from pathlib import Path

random.seed(42)
PEAK_DAYS = [date(2024, 10, 5), date(2025, 11, 30)]

# menu_item_id, menu_item_name, menu_price
MENU_ITEMS = [
    (1, "The Original Orange Chicken", 7.45),
    (2, "Honey Walnut Shrimp (Premium)", 8.40),
    (3, "Beijing Beef", 8.50),
    (4, "Broccoli Beef", 7.95),
    (5, "Grilled Teriyaki Chicken", 7.30),
    (6, "Kung Pao Chicken", 7.15),
    (7, "Honey Sesame Chicken Breast", 7.75),
    (8, "Black Pepper Chicken", 7.15),
    (9, "Mushroom Chicken", 7.10),
    (10, "String Bean Chicken Breast", 7.65),
    (11, "SweetFire Chicken Breast", 7.60),
    (12, "Black Pepper Sirloin Steak (Premium)", 8.80),
    (13, "Chow Mein", 5.45),
    (14, "Fried Rice", 5.15),
    (15, "Super Greens", 5.15),
    (16, "White Steamed Rice", 3.95),
    (17, "Brown Steamed Rice", 3.95),
    (18, "Chicken Egg Roll", 1.95),
    (19, "Veggie Spring Roll", 1.75),
    (20, "Cream Cheese Rangoon", 2.35),
    (21, "Chicken Potstickers", 4.95),
    (22, "Apple Pie Roll", 2.25),
    (23, "Soda", 2.19),
]

# ingredient_id, ingredient_name, qty_in_stock, storage_type, ingredient_cost, measurement_units
INGREDIENTS = [
    (1, "Rice", 180.0, "pantry", 0.55, "lb"),
    (2, "Brown Rice", 170.0, "pantry", 0.60, "lb"),
    (3, "Chicken", 75.0, "refrigerator", 2.25, "lb"),
    (4, "Beef", 42.0, "freezer", 3.30, "lb"),
    (5, "Shrimp", 32.0, "freezer", 3.80, "lb"),
    (6, "Broccoli", 80.0, "refrigerator", 0.70, "lb"),
    (7, "Mushrooms", 48.0, "refrigerator", 0.95, "lb"),
    (8, "Green Beans", 60.0, "refrigerator", 0.85, "lb"),
    (9, "Bell Pepper", 70.0, "refrigerator", 0.60, "lb"),
    (10, "Onion", 85.0, "pantry", 0.30, "lb"),
    (11, "Carrots", 95.0, "refrigerator", 0.45, "lb"),
    (12, "Pineapple", 45.0, "refrigerator", 0.90, "lb"),
    (13, "Orange Sauce", 90.0, "pantry", 0.75, "bottle"),
    (14, "Walnuts", 35.0, "pantry", 1.10, "lb"),
    (15, "Honey", 50.0, "pantry", 0.90, "bottle"),
    (16, "Sesame Seeds", 25.0, "pantry", 0.50, "oz"),
    (17, "Soy Sauce", 95.0, "pantry", 0.35, "bottle"),
    (18, "Chow Mein Noodles", 110.0, "pantry", 0.80, "lb"),
    (19, "Egg Roll Wrapper", 70.0, "pantry", 0.25, "each"),
    (20, "Spring Roll Wrapper", 70.0, "pantry", 0.40, "each"),
    (21, "Cream Cheese", 30.0, "refrigerator", 1.20, "lb"),
    (22, "Wonton Wrapper", 75.0, "pantry", 0.30, "each"),
    (23, "Apple Pie Filling", 40.0, "refrigerator", 0.85, "lb"),
    (24, "Cucumber", 60.0, "refrigerator", 0.40, "lb"),
    (25, "Cabbage", 65.0, "refrigerator", 0.35, "lb"),
    (26, "Tomato", 55.0, "refrigerator", 0.50, "lb"),
    (27, "Garlic", 45.0, "pantry", 0.20, "head"),
    (28, "Ginger", 35.0, "refrigerator", 0.25, "lb"),
    (29, "Egg", 80.0, "refrigerator", 0.35, "each"),
    (30, "Sweet Chili Sauce", 55.0, "pantry", 0.70, "bottle"),
    (31, "Teriyaki Sauce", 60.0, "pantry", 0.65, "bottle"),
    (32, "Black Pepper", 28.0, "pantry", 0.22, "oz"),
    (33, "Carbonated Water", 200.0, "pantry", 0.08, "liter"),
    (34, "Soda Syrup", 120.0, "pantry", 0.90, "liter"),
]

MENU_INGREDIENT_MAP = {
    "The Original Orange Chicken": ["Chicken", "Rice", "Broccoli", "Carrots", "Orange Sauce"],
    "Honey Walnut Shrimp (Premium)": ["Shrimp", "Walnuts", "Honey", "Rice", "Broccoli"],
    "Beijing Beef": ["Beef", "Bell Pepper", "Onion", "Rice", "Soy Sauce"],
    "Broccoli Beef": ["Beef", "Broccoli", "Onion", "Rice", "Soy Sauce"],
    "Grilled Teriyaki Chicken": ["Chicken", "Rice", "Broccoli", "Carrots", "Teriyaki Sauce"],
    "Kung Pao Chicken": ["Chicken", "Bell Pepper", "Onion", "Rice", "Soy Sauce"],
    "Honey Sesame Chicken Breast": ["Chicken", "Rice", "Broccoli", "Honey", "Sesame Seeds"],
    "Black Pepper Chicken": ["Chicken", "Bell Pepper", "Onion", "Rice", "Black Pepper"],
    "Mushroom Chicken": ["Chicken", "Mushrooms", "Onion", "Rice", "Broccoli"],
    "String Bean Chicken Breast": ["Chicken", "Green Beans", "Onion", "Rice", "Soy Sauce"],
    "SweetFire Chicken Breast": ["Chicken", "Bell Pepper", "Pineapple", "Onion", "Sweet Chili Sauce"],
    "Black Pepper Sirloin Steak (Premium)": ["Beef", "Bell Pepper", "Onion", "Rice", "Black Pepper"],
    "Chow Mein": ["Chow Mein Noodles", "Cabbage", "Carrots", "Onion", "Soy Sauce"],
    "Fried Rice": ["Rice", "Egg", "Carrots", "Onion", "Soy Sauce"],
    "Super Greens": ["Broccoli", "Green Beans", "Carrots", "Mushrooms", "Rice"],
    "White Steamed Rice": ["Rice"],
    "Brown Steamed Rice": ["Brown Rice"],
    "Chicken Egg Roll": ["Chicken", "Cabbage", "Carrots", "Egg Roll Wrapper", "Soy Sauce"],
    "Veggie Spring Roll": ["Spring Roll Wrapper", "Cabbage", "Carrots", "Cucumber", "Onion"],
    "Cream Cheese Rangoon": ["Cream Cheese", "Wonton Wrapper", "Egg"],
    "Chicken Potstickers": ["Chicken", "Wonton Wrapper", "Cabbage", "Ginger", "Garlic"],
    "Apple Pie Roll": ["Apple Pie Filling", "Wonton Wrapper", "Egg"],
    "Soda": ["Carbonated Water", "Soda Syrup"],
}


def iso_timestamp(value: datetime) -> str:
    return value.isoformat(timespec="seconds")


def random_datetime(start_date: datetime, end_date: datetime) -> datetime:
    delta_seconds = int((end_date - start_date).total_seconds())
    random_seconds = random.randint(0, max(delta_seconds, 1))
    return start_date + timedelta(seconds=random_seconds)


def format_sql_value(value):
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "TRUE" if value else "FALSE"
    if isinstance(value, (int, float)):
        return str(value)
    if isinstance(value, datetime):
        return "'" + iso_timestamp(value).replace("T", " ") + "'"
    return "'" + str(value).replace("'", "''") + "'"


def build_insert_sql(table_name, columns, rows):
    if not rows:
        return ""
    value_rows = []
    for row in rows:
        value_rows.append("(" + ", ".join(format_sql_value(v) for v in row) + ")")
    return f"INSERT INTO {table_name} ({', '.join(columns)}) VALUES\n" + ",\n".join(value_rows) + ";\n\n"


def generate_menu_items():
    return [
        {
            "menu_item_id": item_id,
            "menu_item_name": name,
            "menu_price": round(price, 2),
        }
        for item_id, name, price in MENU_ITEMS
    ]


def build_ingredient_rows():
    ingredient_rows = []
    for ingredient_id, name, qty_in_stock, storage_type, ingredient_cost, measurement_units in INGREDIENTS:
        ingredient_rows.append(
            {
                "ingredient_id": ingredient_id,
                "ingredient_name": name,
                "qty_in_stock": round(random.uniform(qty_in_stock * 0.85, qty_in_stock * 1.15), 2),
                "storage_type": storage_type,
                "ingredient_cost": round(ingredient_cost + random.uniform(-0.15, 0.35), 2),
                "expiry_date": random_datetime(
                    datetime(2025, 1, 1),
                    datetime(2026, 12, 31),
                ),
                "min_qty_on_hand": round(qty_in_stock * 0.2 + random.uniform(2, 8), 2),
                "measurement_units": measurement_units,
            }
        )
    return ingredient_rows


def generate_menu_item_ingredient_rows(menu_items, ingredients):
    ingredient_lookup = {item[1]: item[0] for item in INGREDIENTS}
    rows = []
    menu_item_ingredient_id = 1

    for menu_item in menu_items:
        menu_name = menu_item["menu_item_name"]
        ingredient_names = MENU_INGREDIENT_MAP.get(menu_name, [random.choice([x[1] for x in INGREDIENTS])])
        selected_ingredients = random.sample(ingredient_names, k=random.randint(1, min(len(ingredient_names), 6)))

        for ingredient_name in selected_ingredients:
            ingredient_id = ingredient_lookup[ingredient_name]
            ingredient_qty = round(random.uniform(0.35, 2.5), 2)
            premium_charge = round(random.uniform(0.0, 2.5), 2) if random.random() < 0.25 else 0.0
            rows.append(
                {
                    "menu_item_ingredient_id": menu_item_ingredient_id,
                    "menu_item_id": menu_item["menu_item_id"],
                    "ingredient_id": ingredient_id,
                    "ingredient_qty": ingredient_qty,
                    "premium_charge": premium_charge,
                }
            )
            menu_item_ingredient_id += 1

    return rows


def generate_orders(order_count=10):
    start_date = datetime(2024, 1, 1, 10, 0, 0)
    end_date = datetime(2025, 12, 31, 21, 0, 0)
    rows = []
    for order_id in range(1, order_count + 1):
        created_at = random_datetime(start_date, end_date)
        while created_at.weekday() >= 5 or created_at.hour < 10 or created_at.hour >= 21:
            created_at = random_datetime(start_date, end_date)

        status = random.choices(["paid", "canceled", "refunded"], weights=[75, 10, 15], k=1)[0]
        payment_method = random.choice(["credit_card", "debit_card", "cash", "gift_card", "meal_pass"])
        updated_at = None
        if status != "paid" or random.random() < 0.2:
            updated_at = created_at + timedelta(hours=random.randint(1, 72))

        rows.append(
            {
                "order_id": order_id,
                "order_status": status,
                "created_at": created_at,
                "updated_at": updated_at,
                "payment_method": payment_method,
                "order_total": 0.0,
            }
        )

    for peak in PEAK_DAYS:
        for r in [r for r in rows if r["created_at"].date() == peak]:
            rows.append({**r, "order_id": len(rows) + 1})

    return rows


def generate_order_details(orders, menu_items):
    rows = []
    order_details_id = 1
    item_lookup = {item["menu_item_id"]: item for item in menu_items}

    for order in orders:
        menu_item_id = random.choice([item["menu_item_id"] for item in menu_items])
        order_total = 0.0

        qty = random.randint(1, 3)
        menu_price = item_lookup[menu_item_id]["menu_price"]
        order_total = round(menu_price * qty, 2)
        rows.append(
            {
                "order_detail_id": order_details_id,
                "menu_item_id": menu_item_id,
                "order_id": order["order_id"],
                "order_qty": qty,
            }
        )
        order_details_id += 1

        order["order_total"] = order_total

    return rows


def build_sql_script(order_count=10):
    menu_items = generate_menu_items()
    ingredient_rows = build_ingredient_rows()
    orders = generate_orders(order_count)
    order_detail_rows = generate_order_details(orders, menu_items)
    menu_item_ingredient_rows = generate_menu_item_ingredient_rows(menu_items, ingredient_rows)

    sql_script = ""
    sql_script += build_insert_sql(
        "menu_items",
        ["menu_item_id", "menu_item_name", "menu_price"],
        [
            (row["menu_item_id"], row["menu_item_name"], row["menu_price"])
            for row in menu_items
        ],
    )
    sql_script += build_insert_sql(
        "ingredients",
        ["ingredient_id", "ingredient_name", "qty_in_stock", "storage_type", "ingredient_cost", "expiry_date", "min_qty_on_hand", "measurement_units"],
        [
            (
                row["ingredient_id"],
                row["ingredient_name"],
                row["qty_in_stock"],
                row["storage_type"],
                row["ingredient_cost"],
                row["expiry_date"],
                row["min_qty_on_hand"],
                row["measurement_units"],
            )
            for row in ingredient_rows
        ],
    )
    sql_script += build_insert_sql(
        "orders",
        ["order_id", "order_status", "created_at", "updated_at", "payment_method", "order_total"],
        [
            (
                row["order_id"],
                row["order_status"],
                row["created_at"],
                row["updated_at"],
                row["payment_method"],
                row["order_total"],
            )
            for row in orders
        ],
    )
    sql_script += build_insert_sql(
        "order_details",
        ["order_detail_id", "menu_item_id", "order_id", "order_qty"],
        [
            (row["order_detail_id"], row["menu_item_id"], row["order_id"], row["order_qty"])
            for row in order_detail_rows
        ],
    )
    sql_script += build_insert_sql(
        "menu_item_ingredients",
        ["menu_item_ingredient_id", "menu_item_id", "ingredient_id", "ingredient_qty", "premium_charge"],
        [
            (
                row["menu_item_ingredient_id"],
                row["menu_item_id"],
                row["ingredient_id"],
                row["ingredient_qty"],
                row["premium_charge"],
            )
            for row in menu_item_ingredient_rows
        ],
    )

    return sql_script


def main():
    order_count = 10
    script = build_sql_script(order_count=order_count)
    output_path = Path(__file__).with_name("generated_data.sql")
    output_path.write_text(script, encoding="utf-8")
    print(f"Generated SQL script with {order_count} orders written to: {output_path}")
    print(f"Rows generated: {script.count('INSERT INTO')}")


if __name__ == "__main__":
    main()
