Java Coffee POS

A simple Coffee Shop Point-of-Sale System built using Java (NetBeans) and MySQL (XAMPP). Import the database file coffee_management.sql into XAMPP/phpMyAdmin (database name: coffee_management).

Features

Admin

Inventory management

User management (add/update/delete users)

Sales reports

Menu & pricing control

Cashier

Menu display

Order processing

Payment & change computation

Receipt generation

Saves transaction records

Requirements

Java JDK 8+

NetBeans IDE

XAMPP (MySQL enabled)

MySQL Connector/J (JDBC driver)

Setup

Start MySQL in XAMPP

Go to phpMyAdmin → Create database: coffee_management

Import: coffee_management.sql

Open project in NetBeans

Update DB connection:

String url = "jdbc:mysql://localhost:3306/coffee_management";
String user = "root";
String password = "";


Build & run the project.

Notes

Admin and Cashier have separate dashboards.

All sales are automatically saved into the database.
