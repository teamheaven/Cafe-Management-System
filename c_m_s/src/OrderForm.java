import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class OrderForm extends JFrame {
    JComboBox<String> itemDropdown;
    JTextField qtyField, customerNameField;
    JButton addBtn, paymentBtn;
    JTable orderTable;
    DefaultTableModel tableModel;

    ArrayList<Object[]> orderItems = new ArrayList<>();
    String[] menuItems = {"Tea - 50", "Sandwich - 120", "Burger - 150","Espresso - 130","Cappuccino - 140","Latte - 150","Mocha - 160","Hot Chocolate - 110","Iced Coffee - 120"};

    public OrderForm() {
        setTitle("Order Form");
        setSize(500, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setBounds(30, 20, 120, 25);
        add(nameLabel);

        customerNameField = new JTextField();
        customerNameField.setBounds(150, 20, 200, 25);
        add(customerNameField);

        itemDropdown = new JComboBox<>(menuItems);
        itemDropdown.setBounds(30, 60, 200, 25);
        add(itemDropdown);

        qtyField = new JTextField("1");
        qtyField.setBounds(250, 60, 50, 25);
        add(qtyField);

        addBtn = new JButton("Add Item");
        addBtn.setBounds(320, 60, 100, 25);
        add(addBtn);

        tableModel = new DefaultTableModel(new String[]{"Item", "Price", "Qty", "Total"}, 0);
        orderTable = new JTable(tableModel);
        JScrollPane pane = new JScrollPane(orderTable);
        pane.setBounds(30, 100, 400, 200);
        add(pane);

        paymentBtn = new JButton("Proceed to Payment");
        paymentBtn.setBounds(150, 320, 180, 30);
        add(paymentBtn);

        addBtn.addActionListener(_ -> addItem());
        paymentBtn.addActionListener(_ -> proceedToPayment());

        setVisible(true);
    }

    private void addItem() {
        String selected = (String) itemDropdown.getSelectedItem();
        String itemName = selected.split(" - ")[0];
        double price = Double.parseDouble(selected.split(" - ")[1]);
        int qty = Integer.parseInt(qtyField.getText());

        double total = price * qty;
        Object[] row = {itemName, price, qty, total};

        orderItems.add(row);
        tableModel.addRow(row);
    }

    private void proceedToPayment() {
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in order!");
            return;
        }

        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Customer Name.");
            return;
        }

        new PaymentPage(orderItems, customerName);
        dispose(); // Close order form after proceeding
    }

    public static void main(String[] args) {
        new OrderForm();
    }
}
