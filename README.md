# ☕ Java Coffee POS System

A simple Coffee Shop Point-of-Sale (POS) System built using **Java (NetBeans)** and **MySQL (XAMPP)**.  
This application manages coffee shop sales, users, inventory, and reporting with separate roles for **Admin** and **Cashier**.

---

## Features

### Admin
- Inventory management  
- User management (Add / Update / Delete users)  
- Sales reports  
- Menu and pricing control  

### Cashier
- Menu display  
- Order processing  
- Payment and change computation  
- Receipt generation  
- Automatic saving of transaction records  

---

## 🛠 Requirements
- Java JDK 8 or higher  
- NetBeans IDE  
- XAMPP (MySQL enabled)  
- MySQL Connector/J (JDBC Driver)  

---

## ⚙️ Setup Instructions

1. Start **MySQL** in XAMPP  
2. Open **phpMyAdmin**  
3. Create a database named:
4. Import the database file:

5. Open the project in **NetBeans**
6. Update the database connection in your Java code:
```java
String url = "jdbc:mysql://localhost:3306/coffee_management";
String user = "root";
String password = "";


Database

Database Name: coffee_management

Import File: coffee_management.sql
