-- Seed customers and purchase transactions for demo/runtime use.
-- Loaded automatically by Spring after the schema is created.

INSERT INTO customers (id, full_name, email) VALUES
	(1, 'Aarav Sharma', 'aarav.sharma@example.com'),
	(2, 'Priya Iyer', 'priya.iyer@example.com'),
	(3, 'Rohan Mehta', 'rohan.mehta@example.com');

INSERT INTO purchase_transactions (customer_id, transaction_date, amount, merchant_name) VALUES
	(1, '2026-04-03', 84.25, 'Reliance Fresh'),
	(1, '2026-04-21', 120.00, 'Westside'),
	(1, '2026-05-09', 52.40, 'Cafe Coffee Day'),
	(1, '2026-05-26', 211.75, 'Pepperfry'),
	(1, '2026-06-12', 149.99, 'Croma'),
	(2, '2026-04-10', 49.99, 'Apollo Pharmacy'),
	(2, '2026-04-18', 75.00, 'Decathlon'),
	(2, '2026-05-05', 101.00, 'Bata'),
	(2, '2026-06-03', 305.40, 'Tanishq'),
	(3, '2026-04-07', 63.75, 'Crossword'),
	(3, '2026-05-16', 99.99, 'Big Bazaar'),
	(3, '2026-06-14', 180.10, 'MakeMyTrip');
