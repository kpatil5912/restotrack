-- Seed data. Safe to re-run: only inserts when the ingredient table is empty.
-- Ingredients start with zero stock; their opening stock is added as PURCHASE
-- movements below so that current_stock always equals the sum of the ledger.
INSERT INTO ingredient (name, stock_unit, current_stock, cost_per_unit, low_stock_threshold, version)
SELECT * FROM (VALUES
    ('Flour',      'KILOGRAM', 10.0, 40.0,  2.0, 0),
    ('Chicken',    'KILOGRAM',  8.0, 220.0, 2.0, 0),
    ('Tomato',     'KILOGRAM',  5.0, 30.0,  1.0, 0),
    ('Cheese',     'KILOGRAM',  4.0, 400.0, 1.0, 0),
    ('Olive Oil',  'LITRE',     3.0, 500.0, 0.5, 0),
    ('Burger Bun', 'PIECE',    50.0, 8.0,  10.0, 0)
) AS seed(name, stock_unit, current_stock, cost_per_unit, low_stock_threshold, version)
WHERE NOT EXISTS (SELECT 1 FROM ingredient);

-- Opening-stock ledger entries matching the seeded quantities above.
INSERT INTO stock_movement (ingredient_id, type, quantity, reason, created_at)
SELECT i.id, 'PURCHASE', i.current_stock, 'opening stock', now()
FROM ingredient i
WHERE NOT EXISTS (SELECT 1 FROM stock_movement);
