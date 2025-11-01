# Cafe Management System (CMS)

A Java-based desktop application for managing cafe orders, payments, and billing.  
This project includes features for user login, order management, UPI/Cash payment, Stock Management, Order History Page, and PDF receipt generation.

---

## Features

- **User Login & Register:** Simple authentication with username and password.  
- **Order Management:** Select items, quantity, table no, Current order,  Proceed to payment, and add the Customer name.
- **Stock Management:** Select items, update quantity, and manage & update the stock.
- **Payment Options:**
- **UPI Payment:** Scan QR and confirm payment.
- **Cash Payment:** Direct payment option.
- **PDF Bill Generation:** Automatically generates a receipt after payment.  
- **Dashboard:** View orders and reports (future enhancements: daily sales summary).  
- **QR Code Support:** Static scannable QR for UPI payments.
- **Order History:** Used to manage the history, check, or track the history.
- **Menu Page:** Where the user can see the menu of items. 

---

## Screenshots

*(Add screenshots of your login, order form, payment page, and PDF receipt here)*

---

## Prerequisites

- Java JDK 17 or above
- Maven (optional, if you want to manage dependencies)
- Required libraries in `/lib` folder:
  - `mysql-connector-j-9.4.0.jar`
  - `barcodes-7.2.0.jar`
  - `core-3.5.1.jar`
  - `io-7.2.0.jar`
  - `javase-3.5.1.jar`
  - `kernel-7.2.0.jar`
  - `layout-7.2.0.jar`
  - `commons-7.2.0.jar`
  - `slf4j-api-1.7.36.jar`
  - `slf4j-simple-1.7.36.jar`

---

### Compile the application using
javac -cp ".;../lib/*" *.java  

## Run this application
java -cp ".;../lib/*" LoginForm

---

## IF access denied for 'root'@'localhost' (using password: no) occurs, here is the solution -
Go to the SQL command line and run these - 
SELECT user, host, plugin FROM mysql.user WHERE user='root';
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'your_password'; 
FLUSH PRIVILEGES;

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/YourUsername/CafeManagementSystem.git
cd CafeManagementSystem
-
