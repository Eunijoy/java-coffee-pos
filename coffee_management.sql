-- Create Database
CREATE DATABASE IF NOT EXISTS coffee_management;
USE coffee_management;

-- Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Coffee Menu Table
CREATE TABLE coffee_menu (
    coffee_id INT PRIMARY KEY AUTO_INCREMENT,
    coffee_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_path VARCHAR(255),
    stock_quantity INT DEFAULT 0,
    category VARCHAR(50),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders Table
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_status ENUM('pending', 'completed', 'cancelled') DEFAULT 'pending',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Order Items Table
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    coffee_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (coffee_id) REFERENCES coffee_menu(coffee_id)
);

-- Insert Default Admin
INSERT INTO users (username, password, full_name, email, role) 
VALUES ('admin', 'admin123', 'Administrator', 'admin@coffee.com', 'admin');

-- Insert Sample User
INSERT INTO users (username, password, full_name, email, role) 
VALUES ('user1', 'user123', 'Kugay123', 'john@email.com', 'user');

-- Insert Sample Coffee Items (UPDATED TO PNG)
INSERT INTO coffee_menu (coffee_name, description, price, image_path, stock_quantity, category) VALUES
('Espresso', 'Strong and bold coffee shot', 3.50, 'images/espresso.png', 100, 'Hot Coffee'),
('Cappuccino', 'Espresso with steamed milk foam', 4.50, 'images/cappuccino.png', 100, 'Hot Coffee'),
('Latte', 'Smooth espresso with steamed milk', 4.75, 'images/latte.png', 100, 'Hot Coffee'),
('Americano', 'Espresso with hot water', 3.75, 'images/americano.png', 100, 'Hot Coffee'),
('Mocha', 'Chocolate flavored coffee', 5.00, 'images/mocha.png', 100, 'Hot Coffee');