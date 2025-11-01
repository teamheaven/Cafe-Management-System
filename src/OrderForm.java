import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class OrderForm extends JFrame {

    private JTable menuTable, orderTable;
    private DefaultTableModel menuModel, orderModel;
    private JTextField customerField, quantityField, tableNoField;
    private JButton addToOrderBtn, removeBtn, proceedPaymentBtn, backBtn;
    private JLabel totalLabel;
    private String userName;
    private double totalAmount = 0;

    public OrderForm(String userName, ArrayList<Object[]> selectedItems) {
        this.userName = userName;

        setTitle("☕ Cafe Delight - Order Form");
        setSize(950, 680);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 236, 220));

        JLabel header = new JLabel("Place Customer Order", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 24));
        header.setBounds(280, 15, 380, 40);
        header.setForeground(new Color(102, 51, 0));
        add(header);

        JLabel customerLabel = new JLabel("Customer Name:");
        customerLabel.setBounds(60, 80, 140, 30);
        customerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(customerLabel);

        customerField = new JTextField();
        customerField.setBounds(200, 80, 180, 30);
        add(customerField);

        JLabel tableLabel = new JLabel("Table No:");
        tableLabel.setBounds(400, 80, 90, 30);
        tableLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(tableLabel);

        tableNoField = new JTextField();
        tableNoField.setBounds(480, 80, 80, 30);
        add(tableNoField);

        JLabel qtyLabel = new JLabel("Qty:");
        qtyLabel.setBounds(580, 80, 40, 30);
        qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(qtyLabel);

        quantityField = new JTextField();
        quantityField.setBounds(620, 80, 60, 30);
        add(quantityField);

        addToOrderBtn = new JButton(" Add Item");
        addToOrderBtn.setBounds(700, 80, 130, 30);
        addToOrderBtn.setBackground(new Color(139, 69, 19));
        addToOrderBtn.setForeground(Color.WHITE);
        add(addToOrderBtn);

        JLabel menuLabel = new JLabel("☕ Menu");
        menuLabel.setFont(new Font("Serif", Font.BOLD, 18));
        menuLabel.setBounds(60, 120, 200, 30);
        add(menuLabel);

        menuModel = new DefaultTableModel(new Object[]{"Item", "Price (₹)"}, 0);
        menuTable = new JTable(menuModel);
        menuTable.setRowHeight(28);
        JScrollPane menuScroll = new JScrollPane(menuTable);
        menuScroll.setBounds(60, 160, 350, 300);
        add(menuScroll);

        loadMenuFromDB();

        JLabel orderLabel = new JLabel("🧾 Current Order");
        orderLabel.setFont(new Font("Serif", Font.BOLD, 18));
        orderLabel.setBounds(450, 120, 200, 30);
        add(orderLabel);

        orderModel = new DefaultTableModel(new Object[]{"Item", "Price (₹)", "Qty", "Total (₹)"}, 0);
        orderTable = new JTable(orderModel);
        orderTable.setRowHeight(28);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBounds(450, 160, 430, 300);
        add(orderScroll);

        removeBtn = new JButton("🗑 Remove Item");
        removeBtn.setBounds(450, 470, 180, 35);
        removeBtn.setBackground(new Color(205, 133, 63));
        removeBtn.setForeground(Color.WHITE);
        add(removeBtn);

        totalLabel = new JLabel("Total: ₹0.0");
        totalLabel.setFont(new Font("Serif", Font.BOLD, 18));
        totalLabel.setBounds(650, 470, 200, 35);
        totalLabel.setForeground(new Color(102, 51, 0));
        add(totalLabel);

        proceedPaymentBtn = new JButton("💳 Proceed to Payment");
        proceedPaymentBtn.setBounds(620, 520, 250, 45);
        proceedPaymentBtn.setBackground(new Color(139, 69, 19));
        proceedPaymentBtn.setForeground(Color.WHITE);
        proceedPaymentBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(proceedPaymentBtn);

        backBtn = new JButton("⬅ Back to Dashboard");
        backBtn.setBounds(60, 520, 200, 45);
        backBtn.setBackground(new Color(160, 82, 45));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(backBtn);

        addToOrderBtn.addActionListener(e -> addToOrder());
        removeBtn.addActionListener(e -> removeItem());
        proceedPaymentBtn.addActionListener(e -> proceedToPayment());
        backBtn.addActionListener(e -> {
            new Dashboard(userName);
            dispose();
        });

        setVisible(true);
    }

    private void loadMenuFromDB() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name, price FROM menu_items");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                menuModel.addRow(new Object[]{rs.getString("name"), rs.getDouble("price")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading menu from DB:\n" + e.getMessage());
        }
    }

    private void addToOrder() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item!");
            return;
        }

        String item = menuModel.getValueAt(selectedRow, 0).toString();
        double price = Double.parseDouble(menuModel.getValueAt(selectedRow, 1).toString());
        int qty;

        try {
            qty = Integer.parseInt(quantityField.getText());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid quantity!");
            return;
        }

        if (!isStockAvailable(item, qty)) {
            JOptionPane.showMessageDialog(this, "Not enough stock for " + item + "!");
            return;
        }

        double total = price * qty;
        orderModel.addRow(new Object[]{item, price, qty, total});
        totalAmount += total;
        totalLabel.setText("Total: ₹" + totalAmount);
        quantityField.setText("");
    }

    private boolean isStockAvailable(String itemName, int qty) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT stock_qty FROM menu_items WHERE name = ?")) {
            ps.setString(1, itemName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int stock = rs.getInt("stock_qty");
                return stock >= qty;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void removeItem() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            double amount = Double.parseDouble(orderModel.getValueAt(selectedRow, 3).toString());
            totalAmount -= amount;
            orderModel.removeRow(selectedRow);
            totalLabel.setText("Total: ₹" + totalAmount);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove!");
        }
    }

    private void proceedToPayment() {
        if (customerField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter customer name!");
            return;
        }

        if (tableNoField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter table number!");
            return;
        }

        if (orderModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items in the order!");
            return;
        }

        int orderId = saveOrderToDB();
        if (orderId != -1) {
            ArrayList<Object[]> orderItems = new ArrayList<>();
            for (int i = 0; i < orderModel.getRowCount(); i++) {
                orderItems.add(new Object[]{
                        orderModel.getValueAt(i, 0),
                        orderModel.getValueAt(i, 1),
                        orderModel.getValueAt(i, 2),
                        orderModel.getValueAt(i, 3)
                });
            }

            new PaymentPage(userName, orderItems, customerField.getText(), orderId);
            dispose();
        }
    }

    // === FIXED: Save Order + Update Stock + Store Item Names ===
    private int saveOrderToDB() {
        int orderId = -1;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            StringBuilder orderedItemsList = new StringBuilder();
            for (int i = 0; i < orderModel.getRowCount(); i++) {
                orderedItemsList.append(orderModel.getValueAt(i, 0));
                if (i < orderModel.getRowCount() - 1) orderedItemsList.append(", ");
            }

            String sql = "INSERT INTO orders (total_amount, customer_name, table_no, ordered_items) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, totalAmount);
            ps.setString(2, customerField.getText());
            ps.setInt(3, Integer.parseInt(tableNoField.getText()));
            ps.setString(4, orderedItemsList.toString());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) orderId = rs.getInt(1);

            String itemSQL = "INSERT INTO order_items (order_id, item_name, price, quantity, total) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement itemPs = conn.prepareStatement(itemSQL);

            String stockSQL = "UPDATE menu_items SET stock_qty = stock_qty - ? WHERE name = ?";
            PreparedStatement stockPs = conn.prepareStatement(stockSQL);

            for (int i = 0; i < orderModel.getRowCount(); i++) {
                String item = orderModel.getValueAt(i, 0).toString();
                double price = Double.parseDouble(orderModel.getValueAt(i, 1).toString());
                int qty = Integer.parseInt(orderModel.getValueAt(i, 2).toString());
                double total = Double.parseDouble(orderModel.getValueAt(i, 3).toString());

                itemPs.setInt(1, orderId);
                itemPs.setString(2, item);
                itemPs.setDouble(3, price);
                itemPs.setInt(4, qty);
                itemPs.setDouble(5, total);
                itemPs.addBatch();

                stockPs.setInt(1, qty);
                stockPs.setString(2, item);
                stockPs.addBatch();
            }

            itemPs.executeBatch();
            stockPs.executeBatch();

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }

        return orderId;
    }

    public static void main(String[] args) {
        new OrderForm("Admin", new ArrayList<>());
    }
}
