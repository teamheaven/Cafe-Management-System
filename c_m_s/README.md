# Cafe Management System (CMS)

A Java Swing-based POS system for managing cafe orders, payments, and reports.

## Features
- User login with authentication
- Order management with dynamic quantity and total calculation
- Payment via UPI QR or Cash
- Bill generation as PDF
- Dashboard with daily sales summary and reports
- Light/Dark mode toggle

## Libraries Used
- MySQL Connector/J
- iText 7 (PDF generation)
- ZXing (QR code generation)
- Java Swing

## How to Run
1. Make sure all required JAR libraries are in the `lib` folder.
2. Open terminal in project root.
3. Compile: `javac -cp ".;lib/*" *.java`
4. Run: `java -cp ".;lib/*" LoginForm`

## Notes
- Update `DBConnection.java` with your MySQL credentials.
- Payment QR is static (`payment.png` in `src` folder).
