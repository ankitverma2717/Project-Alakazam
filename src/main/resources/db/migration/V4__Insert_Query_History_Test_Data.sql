-- Insert sample data into query_history to test the index suggestion engine.
-- These queries are designed to trigger the suggestion logic.

-- Repeatedly query for customers by email to create a clear candidate for indexing.
INSERT INTO query_history (id, natural_language_query, generated_sql, execution_time_ms, predicted_performance, created_at)
VALUES
  (gen_random_uuid(), 'Find customer with email user1@example.com', 'SELECT * FROM customers WHERE email = ''user1@example.com'';', 10, 'Fast', NOW() - INTERVAL '1 minute'),
  (gen_random_uuid(), 'Find customer with email user2@example.com', 'SELECT * FROM customers WHERE email = ''user2@example.com'';', 12, 'Fast', NOW() - INTERVAL '2 minute'),
  (gen_random_uuid(), 'Find customer with email user3@example.com', 'SELECT * FROM customers WHERE email = ''user3@example.com'';', 8, 'Fast', NOW() - INTERVAL '3 minute'),
  (gen_random_uuid(), 'Find customer with email user4@example.com', 'SELECT * FROM customers WHERE email = ''user4@example.com'';', 15, 'Fast', NOW() - INTERVAL '4 minute'),
  (gen_random_uuid(), 'Find customer with email user5@example.com', 'SELECT * FROM customers WHERE email = ''user5@example.com'';', 11, 'Fast', NOW() - INTERVAL '5 minute'),
  (gen_random_uuid(), 'Find customer with email user6@example.com', 'SELECT * FROM customers WHERE email = ''user6@example.com'';', 9, 'Fast', NOW() - INTERVAL '6 minute'),
  (gen_random_uuid(), 'Find customer with email user7@example.com', 'SELECT * FROM customers WHERE email = ''user7@example.com'';', 13, 'Fast', NOW() - INTERVAL '7 minute'),
  (gen_random_uuid(), 'Find customer with email user8@example.com', 'SELECT * FROM customers WHERE email = ''user8@example.com'';', 14, 'Fast', NOW() - INTERVAL '8 minute'),
  (gen_random_uuid(), 'Find customer with email user9@example.com', 'SELECT * FROM customers WHERE email = ''user9@example.com'';', 10, 'Fast', NOW() - INTERVAL '9 minute'),
  (gen_random_uuid(), 'Find customer with email user10@example.com', 'SELECT * FROM customers WHERE email = ''user10@example.com'';', 12, 'Fast', NOW() - INTERVAL '10 minute'),
  (gen_random_uuid(), 'Find customer with email user11@example.com', 'SELECT * FROM customers WHERE email = ''user11@example.com'';', 8, 'Fast', NOW() - INTERVAL '11 minute'),
  (gen_random_uuid(), 'Find customer with email user12@example.com', 'SELECT * FROM customers WHERE email = ''user12@example.com'';', 15, 'Fast', NOW() - INTERVAL '12 minute'),
  (gen_random_uuid(), 'Find customer with email user13@example.com', 'SELECT * FROM customers WHERE email = ''user13@example.com'';', 11, 'Fast', NOW() - INTERVAL '13 minute'),
  (gen_random_uuid(), 'Find customer with email user14@example.com', 'SELECT * FROM customers WHERE email = ''user14@example.com'';', 9, 'Fast', NOW() - INTERVAL '14 minute'),
  (gen_random_uuid(), 'Find customer with email user15@example.com', 'SELECT * FROM customers WHERE email = ''user15@example.com'';', 13, 'Fast', NOW() - INTERVAL '15 minute');

-- Query for products by category and price, another potential indexing scenario.
INSERT INTO query_history (id, natural_language_query, generated_sql, execution_time_ms, predicted_performance, created_at)
VALUES
  (gen_random_uuid(), 'Find expensive electronics', 'SELECT * FROM products WHERE category_id = 1 AND price > 100;', 25, 'Moderate', NOW() - INTERVAL '1 minute'),
  (gen_random_uuid(), 'Find expensive electronics', 'SELECT * FROM products WHERE category_id = 1 AND price > 100;', 28, 'Moderate', NOW() - INTERVAL '2 minute'),
  (gen_random_uuid(), 'Find expensive electronics', 'SELECT * FROM products WHERE category_id = 1 AND price > 100;', 22, 'Moderate', NOW() - INTERVAL '3 minute'),
  (gen_random_uuid(), 'Find expensive electronics', 'SELECT * FROM products WHERE category_id = 1 AND price > 100;', 30, 'Moderate', NOW() - INTERVAL '4 minute'),
  (gen_random_uuid(), 'Find expensive electronics', 'SELECT * FROM products WHERE category_id = 1 AND price > 100;', 26, 'Moderate', NOW() - INTERVAL '5 minute');

-- A query with a JOIN and a WHERE clause.
INSERT INTO query_history (id, natural_language_query, generated_sql, execution_time_ms, predicted_performance, created_at)
VALUES
  (gen_random_uuid(), 'Show orders for a specific customer', 'SELECT o.* FROM orders o JOIN customers c ON o.customer_id = c.id WHERE c.email = ''user5@example.com'';', 40, 'Moderate', NOW() - INTERVAL '1 minute'),
  (gen_random_uuid(), 'Show orders for a specific customer', 'SELECT o.* FROM orders o JOIN customers c ON o.customer_id = c.id WHERE c.email = ''user5@example.com'';', 45, 'Moderate', NOW() - INTERVAL '2 minute'),
  (gen_random_uuid(), 'Show orders for a specific customer', 'SELECT o.* FROM orders o JOIN customers c ON o.customer_id = c.id WHERE c.email = ''user5@example.com'';', 38, 'Moderate', NOW() - INTERVAL '3 minute');
