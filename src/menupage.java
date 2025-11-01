import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class menupage extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton backBtn;
    private String userName;
    private Image bgImage;

    public menupage(String userName) {
        this.userName = userName;

        setTitle("☕ Cafe Delight - Menu Items");
        setSize(1000, 690);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        try {
            bgImage = new ImageIcon(getClass().getResource("bg2_menu.jpeg")).getImage();
        } catch (Exception e) {
            System.out.println("Background image not found!");
        }

        JLabel background = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null)
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        background.setLayout(null);
        setContentPane(background);

        // === Overlay Panel ===
        JPanel overlay = new JPanel();
        overlay.setBackground(new Color(0, 0, 0, 160));
        overlay.setBounds(0, 0, 1000, 650);
        overlay.setLayout(null);
        background.add(overlay);

        JLabel title = new JLabel("Our Delicious Menu", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 25, 1000, 50);
        overlay.add(title);

        // === Table Setup ===
        model = new DefaultTableModel(new String[]{"Item ID", "Item Name", "Price (₹)"}, 0);
        table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(92, 64, 51));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 170, 120));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(180, 150, 100));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(100, 100, 800, 380);
        overlay.add(scrollPane);

        // === Back Button ===
        backBtn = new JButton("⬅ Back to Dashboard");
        backBtn.setBounds(400, 520, 200, 45);
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(92, 64, 51));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        overlay.add(backBtn);

        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(userName);
        });

        // Load data and display
        loadMenuItems();
        setVisible(true);
    }

    // === Fetch Menu Data ===
    private void loadMenuItems() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(
                     "jdbc:mysql://localhost:3306/cafe_db", "root", "Devesh@2005");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT item_id, name, price FROM menu_items")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading menu items:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new menupage("Customer");
    }
}
