import javax.swing.*;                  
import java.awt.*;                      
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;


public class PaymentPage extends JFrame {
    ArrayList<Object[]> orderItems;
    String customerName, userName;
    int orderId;

    JLabel qrLabel;
    JButton paymentDoneBtn, cashBtn;

    public PaymentPage(String userName, ArrayList<Object[]> orderItems, String customerName, int orderId) {
        this.userName = userName;
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        this.customerName = customerName;
        this.orderId = orderId;

        setTitle("☕ Cafe Payment Portal");
        setSize(520, 680);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 236, 220));

        JLabel titleLabel = new JLabel("Scan & Pay Securely", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setBounds(70, 20, 380, 30);
        titleLabel.setForeground(new Color(102, 51, 0));
        add(titleLabel);

        ImageIcon originalQR = new ImageIcon("payment.png");
        if (originalQR.getIconWidth() == -1) {
            qrLabel = new JLabel("QR Image Missing ☕", SwingConstants.CENTER);
        } else {
            Image scaledQR = originalQR.getImage().getScaledInstance(360, 360, Image.SCALE_SMOOTH);
            qrLabel = new JLabel(new ImageIcon(scaledQR));
        }
        qrLabel.setBounds(75, 70, 360, 360);
        qrLabel.setBorder(BorderFactory.createLineBorder(new Color(150, 75, 0), 2));
        add(qrLabel);

        JLabel info = new JLabel("Scan QR to pay via any UPI app", SwingConstants.CENTER);
        info.setBounds(70, 440, 380, 25);
        info.setFont(new Font("SansSerif", Font.PLAIN, 15));
        add(info);

        paymentDoneBtn = new JButton("Payment Done (UPI)");
        paymentDoneBtn.setBounds(60, 490, 180, 50);
        paymentDoneBtn.setBackground(new Color(139, 69, 19));
        paymentDoneBtn.setForeground(Color.WHITE);
        paymentDoneBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        add(paymentDoneBtn);

        cashBtn = new JButton("💵 Cash Payment");
        cashBtn.setBounds(270, 490, 180, 50);
        cashBtn.setBackground(new Color(205, 133, 63));
        cashBtn.setForeground(Color.WHITE);
        cashBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        add(cashBtn);

        paymentDoneBtn.addActionListener(_ -> processPayment("UPI"));
        cashBtn.addActionListener(_ -> processPayment("Cash"));

        setVisible(true);
    }

    private void processPayment(String method) {
        try {
            updatePaymentInDB(method);
            generateBill(method);
            JOptionPane.showMessageDialog(this, "Payment Successful & Bill Generated!");
            dispose();
            new Dashboard(userName);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updatePaymentInDB(String method) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE orders SET payment_method=?, payment_done=true WHERE order_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, method);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    private void generateBill(String method) throws Exception {
        double totalAmount = 0;
        for (Object[] row : orderItems) totalAmount += Double.parseDouble(row[3].toString());

        new File("bills").mkdirs();
        String pdfFile = "bills/Bill_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("☕ Cafe Delight - Customer Bill").setFontSize(18).setBold());
        document.add(new Paragraph("Customer Name: " + customerName));
        document.add(new Paragraph("Served By: " + userName));
        document.add(new Paragraph("Payment Method: " + method));
        document.add(new Paragraph("Date: " + java.time.LocalDateTime.now()));
        document.add(new Paragraph(" "));

        Table billTable = new Table(new float[]{3, 2, 2, 2});
        billTable.useAllAvailableWidth();
        billTable.addHeaderCell("Item");
        billTable.addHeaderCell("Price");
        billTable.addHeaderCell("Qty");
        billTable.addHeaderCell("Total");

        for (Object[] row : orderItems) {
            billTable.addCell(row[0].toString());
            billTable.addCell(row[1].toString());
            billTable.addCell(row[2].toString());
            billTable.addCell(row[3].toString());
        }

        document.add(billTable);
        document.add(new Paragraph("\nGrand Total: ₹" + totalAmount).setBold());
        document.add(new Paragraph("\nThank you for visiting Cafe Delight! ☕"));
        document.add(new Paragraph("We hope to serve you again soon."));
        document.close();
    }
}
