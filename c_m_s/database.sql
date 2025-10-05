-- ========================
-- Cafe Management System Database
-- ========================

-- Database creation
CREATE DATABASE IF NOT EXISTS cafe_db;
USE cafe_db;

-- ========================
-- Users Table
-- ========================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('admin','staff') DEFAULT 'staff'
);

-- Sample admin and staff users
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'admin'),
('staff1', 'staff123', 'staff');

-- ========================
-- Items Table
-- ========================
CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    stock INT DEFAULT 0
);

-- Sample items
INSERT INTO items (name, price, stock) VALUES 
('Espresso', 100, 50),
('Latte', 120, 50),
('Cappuccino', 150, 50),
('Sandwich', 80, 30),
('Pasta', 180, 20);

-- ========================
-- Orders Table
-- ========================
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    table_no INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    total DOUBLE NOT NULL,
    payment_method ENUM('UPI','Cash') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id)
);

-- Sample order
INSERT INTO orders (customer_name, table_no, item_id, quantity, total, payment_method) VALUES
('John Doe', 1, 1, 2, 200, 'UPI'),
('Jane Doe', 2, 3, 1, 150, 'Cash');
