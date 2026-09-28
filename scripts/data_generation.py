import numpy as np
from datetime import datetime, date

order_id = 0
order_details_id = 0
menu_item_id = 0
menu_item_ingredients_id = 0
ingredients_id = 0


for i in range(365):
    print("yay")

def generate_sorted_timestamps(start_date, end_date, count):
    start = int(start_date.timestamp())
    end = int(end_date.timestamp())

    timestamps = [random.randint(start, end) for _ in range(count)]
    timestamps.sort()

    return [datetime.fromtimestamp(i) for i in timestamps]