import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class OrderHistoryPage extends JFrame {

    private JTable orderTable, stockTable;
    private DefaultTableModel orderModel, stockModel;
    private JButton manageStockBtn, reloadBtn, logoutBtn;

    public OrderHistoryPage() {
        setTitle("Cafe Delight - Order History & Stock Management");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ====== HEADER PANEL ======
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 60, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("☕ Cafe Delight - Order History & Stock Management", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        headerPanel.add(header, BorderLayout.CENTER);

        // ====== BUTTON PANEL ======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.setOpaque(false);

        manageStockBtn = new JButton("📦 Manage Stock");
        reloadBtn = new JButton("🔄 Reload Data");
        logoutBtn = new JButton("🚪 Logout");

        // Button styling
        JButton[] buttons = {manageStockBtn, reloadBtn, logoutBtn};
        for (JButton b : buttons) {
            b.setFocusPainted(false);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setBackground(new Color(139, 69, 19));
            b.setForeground(Color.WHITE);
            b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        buttonPanel.add(manageStockBtn);
        buttonPanel.add(reloadBtn);
        buttonPanel.add(logoutBtn);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ====== TABLES SECTION ======
        String[] orderCols = {"Order ID", "Customer Name", "Table No", "Total Amount", "Ordered Items", "Payment Method", "Payment Done", "Order Date"};
        orderModel = new DefaultTableModel(orderCols, 0);
        orderTable = new JTable(orderModel);
        orderTable.setRowHeight(25);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(BorderFactory.createTitledBorder("🧾 Order History"));

        String[] stockCols = {"Item ID", "Item Name", "Price (₹)", "Stock Quantity"};
        stockModel = new DefaultTableModel(stockCols, 0);
        stockTable = new JTable(stockModel);
        stockTable.setRowHeight(25);
        JScrollPane stockScroll = new JScrollPane(stockTable);
        stockScroll.setBorder(BorderFactory.createTitledBorder("📦 Current Menu Stock"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, orderScroll, stockScroll);
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        // ====== BUTTON ACTIONS ======
        reloadBtn.addActionListener(e -> {
            loadOrderData();
            loadStockData();
            JOptionPane.showMessageDialog(this, "Data reloaded successfully!", "Reload", JOptionPane.INFORMATION_MESSAGE);
        });

        manageStockBtn.addActionListener(e -> manageStock());
        logoutBtn.addActionListener(e -> logout());

        // Load data initially
        loadOrderData();
        loadStockData();
    }

    private void loadOrderData() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT order_id, customer_name, table_no, total_amount, ordered_items, payment_method, payment_done, order_date FROM orders ORDER BY order_date DESC");
             ResultSet rs = ps.executeQuery()) {

            orderModel.setRowCount(0);
            while (rs.next()) {
                orderModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getInt("table_no"),
                        rs.getDouble("total_amount"),
                        rs.getString("ordered_items"),
                        rs.getString("payment_method"),
                        rs.getInt("payment_done") == 1 ? "Yes" : "No",
                        rs.getString("order_date")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStockData() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT item_id, name, price, stock_qty FROM menu_items ORDER BY item_id");
             ResultSet rs = ps.executeQuery()) {

            stockModel.setRowCount(0);
            while (rs.next()) {
                stockModel.addRow(new Object[]{
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_qty")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading stock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manageStock() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the stock table!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemId = (int) stockModel.getValueAt(selectedRow, 0);
        String itemName = stockModel.getValueAt(selectedRow, 1).toString();
        int currentStock = (int) stockModel.getValueAt(selectedRow, 3);

        String newStockStr = JOptionPane.showInputDialog(this, "Enter new stock quantity for " + itemName + ":", currentStock);
        if (newStockStr == null || newStockStr.isEmpty()) return;

        try {
            int newStock = Integer.parseInt(newStockStr);
            if (newStock < 0) throw new NumberFormatException();

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE menu_items SET stock_qty = ? WHERE item_id = ?")) {
                ps.setInt(1, newStock);
                ps.setInt(2, itemId);
                ps.executeUpdate();
            }

            loadStockData();
            JOptionPane.showMessageDialog(this, "Stock updated successfully for " + itemName + "!", "Stock Updated", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating stock: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm();
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderHistoryPage().setVisible(true));
    }
}
