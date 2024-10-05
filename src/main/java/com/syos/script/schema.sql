-- Create the database (if not exists)
CREATE DATABASE IF NOT EXISTS syos_db_dinil;

-- Use the database
USE syos_db_dinil;

-- 1. Users Table
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Inventory Table
CREATE TABLE Inventory (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    discount_value DECIMAL(10, 2) DEFAULT 0.00,
    discount_strategy VARCHAR(50) DEFAULT 'NONE',  -- Strategy can be 'FIXED' or 'PERCENTAGE' or 'NONE'
    store_stock INT DEFAULT 0, -- Stock for store purchases
    online_stock INT DEFAULT 0, -- Stock for online purchases
    shelf_capacity INT NOT NULL -- Maximum items that can be stocked on a shelf
);

-- 3. Transactions Table
CREATE TABLE Transaction (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_type VARCHAR(50) NOT NULL,
    user_id INT, -- Null if it's over-the-counter
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE SET NULL
);

-- 4. Billing Table
CREATE TABLE Bill (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    cash_tendered DECIMAL(10, 2), -- Cash tendered for store payments
    change_amount DECIMAL(10, 2), -- Change given back in store payments
    FOREIGN KEY (transaction_id) REFERENCES Transaction(transaction_id)
);

-- 5. Bill Items Table
CREATE TABLE Bill_Item (
    bill_item_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    item_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES Bill(bill_id),
    FOREIGN KEY (item_id) REFERENCES Inventory(item_id)
);

-- 6. Stock Batches Table
CREATE TABLE Stock_Batch (
    batch_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    date_received DATE NOT NULL,
    expiry_date DATE NOT NULL,
    FOREIGN KEY (item_id) REFERENCES Inventory(item_id)
);

-- Insert Users
INSERT INTO User (name, email, password)
VALUES
('John Doe', 'john.doe@example.com', 'password123!'),
('Jane Smith', 'jane.smith@example.com', 'securePass!456'),
('Ahmed Khan', 'ahmed.khan@example.com', 'AKhan@2024'),
('Sara Lee', 'sara.lee@example.com', 'LeeS12345');

-- Insert Inventory with Better Item Codes and More Items
INSERT INTO Inventory (item_code, name, price, discount_value, discount_strategy, store_stock, online_stock, shelf_capacity)
VALUES
('F001', 'Apple 1kg', 150.00, 0.00, 'NONE', 100, 150, 200),
('F002', 'Banana 1 dozen', 60.00, 0.00, 'NONE', 80, 120, 180),
('S001', 'Chips 200g', 50.00, 10.00, 'PERCENTAGE', 200, 300, 400),
('S002', 'Biscuits 300g', 80.00, 5.00, 'PERCENTAGE', 150, 250, 300),
('B001', 'Bread 500g', 80.00, 0.00, 'NONE', 60, 80, 100),
('B002', 'Brown Bread 500g', 100.00, 10.00, 'FIXED', 50, 60, 100),
('D001', 'Milk 1L', 100.00, 10.00, 'PERCENTAGE', 200, 250, 300),
('D002', 'Butter 500g', 250.00, 20.00, 'FIXED', 100, 120, 150),
('G001', 'Rice 5kg', 800.00, 0.00, 'NONE', 50, 70, 100),
('G002', 'Wheat Flour 5kg', 400.00, 0.00, 'NONE', 40, 50, 80),
('B003', 'Croissant 2pcs', 120.00, 0.00, 'NONE', 70, 90, 100),
('S003', 'Peanuts 250g', 60.00, 0.00, 'NONE', 150, 200, 300),
('S004', 'Chocolate Bar 100g', 100.00, 10.00, 'PERCENTAGE', 180, 220, 250),
('F003', 'Grapes 1kg', 200.00, 0.00, 'NONE', 100, 120, 150),
('F004', 'Orange 1kg', 140.00, 0.00, 'NONE', 90, 110, 130);

-- Insert Stock Batches with More Entries for Each Item
INSERT INTO Stock_Batch (item_id, quantity, date_received, expiry_date)
VALUES
-- Apple Batches
(1, 150, '2024-09-25', '2024-12-25'), -- Batch 1 for Apple
(1, 120, '2024-10-01', '2024-12-30'), -- Batch 2 for Apple
(1, 180, '2024-10-03', '2025-01-05'), -- Batch 3 for Apple

-- Banana Batches
(2, 120, '2024-09-28', '2024-10-10'), -- Batch 1 for Banana
(2, 140, '2024-09-30', '2024-10-15'), -- Batch 2 for Banana
(2, 160, '2024-10-02', '2024-10-20'), -- Batch 3 for Banana

-- Chips Batches
(3, 300, '2024-09-27', '2025-03-27'), -- Batch 1 for Chips
(3, 350, '2024-09-29', '2025-04-01'), -- Batch 2 for Chips
(3, 320, '2024-10-01', '2025-04-15'), -- Batch 3 for Chips

-- Biscuits Batches
(4, 250, '2024-09-30', '2025-02-28'), -- Batch 1 for Biscuits
(4, 270, '2024-10-01', '2025-03-01'), -- Batch 2 for Biscuits
(4, 260, '2024-10-03', '2025-03-10'), -- Batch 3 for Biscuits

-- Bread Batches
(5, 80, '2024-09-28', '2024-11-28'),  -- Batch 1 for Bread
(5, 100, '2024-09-30', '2024-12-01'), -- Batch 2 for Bread
(5, 90, '2024-10-02', '2024-12-10'),  -- Batch 3 for Bread

-- Brown Bread Batches
(6, 60, '2024-09-29', '2024-11-29'),  -- Batch 1 for Brown Bread
(6, 70, '2024-10-01', '2024-12-01'),  -- Batch 2 for Brown Bread
(6, 80, '2024-10-03', '2024-12-05'),  -- Batch 3 for Brown Bread

-- Milk Batches
(7, 250, '2024-09-25', '2024-12-25'), -- Batch 1 for Milk
(7, 300, '2024-09-27', '2024-12-28'), -- Batch 2 for Milk
(7, 280, '2024-10-01', '2024-12-30'), -- Batch 3 for Milk

-- Butter Batches
(8, 120, '2024-09-27', '2024-11-30'), -- Batch 1 for Butter
(8, 140, '2024-09-29', '2024-12-15'), -- Batch 2 for Butter
(8, 130, '2024-10-02', '2024-12-20'), -- Batch 3 for Butter

-- Rice Batches
(9, 70, '2024-09-26', '2025-06-30'),  -- Batch 1 for Rice
(9, 100, '2024-09-28', '2025-07-01'), -- Batch 2 for Rice
(9, 90, '2024-10-02', '2025-07-15'),  -- Batch 3 for Rice

-- Wheat Flour Batches
(10, 50, '2024-09-27', '2025-01-27'), -- Batch 1 for Wheat Flour
(10, 60, '2024-09-29', '2025-02-01'), -- Batch 2 for Wheat Flour
(10, 70, '2024-10-03', '2025-02-15'), -- Batch 3 for Wheat Flour

-- Croissant Batches
(11, 90, '2024-09-26', '2024-10-10'), -- Batch 1 for Croissant
(11, 100, '2024-09-29', '2024-10-15'),-- Batch 2 for Croissant
(11, 110, '2024-10-01', '2024-10-20'),-- Batch 3 for Croissant

-- Peanuts Batches
(12, 10, '2024-09-30', '2025-06-30'), -- Batch 1 for Peanuts
(12, 20, '2024-10-01', '2025-07-01'), -- Batch 2 for Peanuts
(12, 10, '2024-10-03', '2025-07-15'), -- Batch 3 for Peanuts

-- Chocolate Bar Batches
(13, 10, '2024-09-25', '2025-09-25'), -- Batch 1 for Chocolate Bar
(13, 10, '2024-09-30', '2025-10-01'), -- Batch 2 for Chocolate Bar
(13, 10, '2024-10-02', '2025-10-15'), -- Batch 3 for Chocolate Bar

-- Grapes Batches
(14, 35, '2024-10-01', '2024-10-31'), -- Batch 1 for Grapes
(14, 20, '2024-10-03', '2024-11-05'), -- Batch 2 for Grapes
(14, 10, '2024-10-05', '2024-11-10'), -- Batch 3 for Grapes

-- Orange Batches
(15, 10, '2024-10-02', '2024-11-15'), -- Batch 1 for Orange
(15, 10, '2024-10-03', '2024-11-20'), -- Batch 2 for Orange
(15, 30, '2024-10-05', '2024-11-25'); -- Batch 3 for Orange

-- Insert Transactions with More Current Date and Other Date Transactions
INSERT INTO Transaction (transaction_type, user_id, total_amount, created_at)
VALUES
('online', 1, 320.00, '2024-09-25 10:15:00'),
('store', NULL, 420.00, '2024-09-26 11:30:00'),
('online', 2, 700.00, '2024-09-27 12:00:00'),
('store', NULL, 300.00, '2024-09-28 14:45:00'),
('online', 3, 580.00, '2024-09-29 15:00:00'),
('store', NULL, 150.00, '2024-09-30 16:15:00'),
('online', 1, 750.00, '2024-10-01 17:30:00'),
('store', NULL, 960.00, '2024-10-02 18:00:00'),
('online', 4, 880.00, '2024-10-03 19:45:00'),
('store', NULL, 620.00, CURRENT_TIMESTAMP),
('online', 1, 460.00, CURRENT_TIMESTAMP),
('online', 2, 650.00, CURRENT_TIMESTAMP),
('store', NULL, 550.00, '2024-09-29 11:00:00'),
('store', NULL, 850.00, CURRENT_TIMESTAMP),
('online', 3, 950.00, CURRENT_TIMESTAMP),
('store', NULL, 780.00, '2024-09-28 13:30:00');

-- Insert Bills for Transactions
INSERT INTO Bill (transaction_id, total_amount, cash_tendered, change_amount)
VALUES
(1, 320.00, NULL, NULL),
(2, 420.00, 500.00, 80.00),
(3, 700.00, NULL, NULL),
(4, 300.00, 350.00, 50.00),
(5, 580.00, NULL, NULL),
(6, 150.00, 200.00, 50.00),
(7, 750.00, NULL, NULL),
(8, 960.00, 1000.00, 40.00),
(9, 880.00, NULL, NULL),
(10, 620.00, 700.00, 80.00),
(11, 460.00, NULL, NULL),
(12, 650.00, NULL, NULL),
(13, 550.00, 600.00, 50.00),
(14, 850.00, 900.00, 50.00),
(15, 950.00, NULL, NULL),
(16, 780.00, 800.00, 20.00);

-- Insert Bill Items for Each Bill
INSERT INTO Bill_Item (bill_id, item_id, quantity, item_price, total_price)
VALUES
(1, 1, 2, 150.00, 300.00), -- Apple
(1, 2, 1, 60.00, 60.00),  -- Banana

(2, 3, 4, 50.00, 200.00), -- Chips
(2, 5, 2, 80.00, 160.00), -- Bread

(3, 7, 3, 100.00, 300.00), -- Milk
(3, 8, 1, 250.00, 250.00), -- Butter

(4, 12, 5, 60.00, 300.00), -- Peanuts

(5, 6, 4, 100.00, 400.00), -- Brown Bread

(6, 5, 2, 80.00, 160.00), -- Bread

(7, 14, 4, 200.00, 800.00), -- Grapes

(8, 3, 6, 50.00, 300.00), -- Chips

(9, 4, 5, 80.00, 400.00), -- Biscuits

(10, 13, 3, 100.00, 300.00), -- Chocolate Bar

(11, 2, 5, 60.00, 300.00), -- Banana

(12, 8, 2, 250.00, 500.00), -- Butter

(13, 9, 1, 800.00, 800.00), -- Rice

(14, 7, 3, 100.00, 300.00), -- Milk

(15, 1, 4, 150.00, 600.00), -- Apple

(16, 4, 2, 80.00, 160.00); -- Biscuits





