DO $$ -- This starts a block of procedural code
DECLARE
i INT;
    random_customer_id INT;
    random_product_id INT;
    random_order_id INT;
    items_in_order INT;
    j INT;
BEGIN
    -- Insert Categories
INSERT INTO categories (name) VALUES
                                  ('Electronics'), ('Books'), ('Home & Garden'), ('Clothing'), ('Sports');

-- Insert 100 Customers
FOR i IN 1..100 LOOP
        INSERT INTO customers (first_name, last_name, email, registration_date)
        VALUES (
            'User' || i,
            'Test',
            'user' || i || '@example.com',
            NOW() - (random() * 365) * INTERVAL '1 day'
        );
END LOOP;

    -- Insert 50 Products
FOR i IN 1..50 LOOP
        INSERT INTO products (name, description, price, stock_quantity, category_id)
        VALUES (
            'Product ' || i,
            'Description for product ' || i,
            (random() * 100 + 10)::DECIMAL(10,2),
            (random() * 200 + 1)::INT,
            (SELECT id FROM categories ORDER BY random() LIMIT 1) -- Assigns a random category
        );
END LOOP;

    -- Insert 200 Orders
FOR i IN 1..200 LOOP
        -- Select a random customer that exists
SELECT id INTO random_customer_id FROM customers ORDER BY random() LIMIT 1;

INSERT INTO orders (customer_id, order_date, status)
VALUES (
           random_customer_id,
           NOW() - (random() * 90) * INTERVAL '1 day',
           (ARRAY['pending', 'shipped', 'delivered', 'cancelled'])[floor(random() * 4 + 1)]
       ) RETURNING id INTO random_order_id; -- Get the ID of the order we just created

-- For each order, add 1 to 3 items
items_in_order := floor(random() * 3 + 1);
FOR j IN 1..items_in_order LOOP
SELECT id INTO random_product_id FROM products ORDER BY random() LIMIT 1;

INSERT INTO order_items (order_id, product_id, quantity, price_per_unit)
VALUES (
           random_order_id,
           random_product_id,
           floor(random() * 5 + 1),
           (SELECT price FROM products WHERE id = random_product_id) -- Use the product's current price
       );
END LOOP;
END LOOP;

END $$; -- This ends the block