-- Table for the application to store its own query history
CREATE TABLE query_history (
                               id UUID PRIMARY KEY,
                               natural_language_query TEXT NOT NULL,
                               generated_sql TEXT NOT NULL,
                               execution_time_ms INTEGER,
                               created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- Table for product categories
CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE
);

-- Table for customers
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           registration_date DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Table for products, with a foreign key to categories
CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10, 2) NOT NULL,
                          stock_quantity INT NOT NULL,
                          category_id INT,
                          CONSTRAINT fk_category
                              FOREIGN KEY(category_id)
                                  REFERENCES categories(id)
);

-- Table for orders, with a foreign key to customers
CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        customer_id INT NOT NULL,
                        order_date TIMESTAMP NOT NULL DEFAULT NOW(),
                        status VARCHAR(50) NOT NULL,
                        CONSTRAINT fk_customer
                            FOREIGN KEY(customer_id)
                                REFERENCES customers(id)
);

-- A "join table" for the many-to-many relationship between orders and products
CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             quantity INT NOT NULL,
                             price_per_unit DECIMAL(10, 2) NOT NULL,
                             CONSTRAINT fk_order
                                 FOREIGN KEY(order_id)
                                     REFERENCES orders(id),
                             CONSTRAINT fk_product
                                 FOREIGN KEY(product_id)
                                     REFERENCES products(id)
);