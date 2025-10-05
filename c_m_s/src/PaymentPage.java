import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.util.ArrayList;
import java.io.FileOutputStream;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

public class PaymentPage extends JFrame {
    ArrayList<Object[]> orderItems;
    String customerName;

    JLabel qrLabel;
    JButton paymentDoneBtn, cashBtn;

    public PaymentPage(ArrayList<Object[]> orderItems, String customerName){
        this.orderItems = orderItems;
        this.customerName = customerName;

        setTitle("Payment Page");
        setSize(500, 650);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Title Label
        JLabel titleLabel = new JLabel("Scan to Pay", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(50, 10, 400, 30);
        add(titleLabel);

        // QR Code Image - Using SCALE_REPLICATE for sharp edges
        // Maintaining square aspect ratio (QR codes are always square)
        ImageIcon originalQR = new ImageIcon("payment.png");
        Image scaledQR = originalQR.getImage().getScaledInstance(380, 380, Image.SCALE_REPLICATE);
        qrLabel = new JLabel(new ImageIcon(scaledQR));
        qrLabel.setBounds(60, 50, 380, 380);
        qrLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        add(qrLabel);

        // Instructions
        JLabel instructionLabel = new JLabel("Scan the QR code with any UPI app", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionLabel.setBounds(50, 440, 400, 25);
        add(instructionLabel);

        // Buttons below the QR
        paymentDoneBtn = new JButton("Payment Done (UPI)");
        paymentDoneBtn.setBounds(50, 480, 180, 50);
        paymentDoneBtn.setBackground(new Color(76, 175, 80));
        paymentDoneBtn.setForeground(Color.WHITE);
        paymentDoneBtn.setFocusPainted(false);
        paymentDoneBtn.setFont(new Font("Arial", Font.BOLD, 12));
        add(paymentDoneBtn);

        cashBtn = new JButton("Cash Payment");
        cashBtn.setBounds(270, 480, 180, 50);
        cashBtn.setBackground(new Color(33, 150, 243));
        cashBtn.setForeground(Color.WHITE);
        cashBtn.setFocusPainted(false);
        cashBtn.setFont(new Font("Arial", Font.BOLD, 12));
        add(cashBtn);

        
        paymentDoneBtn.addActionListener(_ -> generateBill("UPI"));
        cashBtn.addActionListener(_ -> generateBill("Cash"));

        setVisible(true);
    }

    private void generateBill(String method){
        try{
            double totalAmount=0;
            for(Object[] row : orderItems){
                totalAmount += Double.parseDouble(row[1].toString()) * Integer.parseInt(row[2].toString());
            }

            String pdfFile = "Bill_"+System.currentTimeMillis()+".pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Cafe POS - Bill").setBold());
            document.add(new Paragraph("Customer Name: " + customerName));
            document.add(new Paragraph("Payment Method: " + method));
            document.add(new Paragraph(" "));

            Table billTable = new Table(4);
            billTable.addHeaderCell("Item");
            billTable.addHeaderCell("Price");
            billTable.addHeaderCell("Qty");
            billTable.addHeaderCell("Total");

            for(Object[] row: orderItems){
                billTable.addCell(row[0].toString()); // Item
                billTable.addCell(row[1].toString()); // Price
                billTable.addCell(row[2].toString()); // Qty
                billTable.addCell(row[3].toString()); // Total
            }

            document.add(billTable);
            document.add(new Paragraph("Total Amount: ₹" + totalAmount));
            document.add(new Paragraph("Thank you for visiting!"));
            document.close();

            JOptionPane.showMessageDialog(this,"Bill Generated: " + pdfFile);
            dispose();
            new OrderForm(); // Reset Order Form

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
