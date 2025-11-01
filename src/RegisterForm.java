import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton registerButton, backButton;

    public RegisterForm() {
        setTitle("☕ Cafe Management System - Register");
        setSize(450, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // === Gradient Background Panel ===
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(245, 230, 200);
                Color color2 = new Color(190, 120, 80);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(null);

        // === Title ===
        JLabel titleLabel = new JLabel("Register Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        titleLabel.setBounds(0, 20, 450, 40);
        titleLabel.setForeground(new Color(60, 30, 10));
        panel.add(titleLabel);

        // === Username ===
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        userLabel.setBounds(80, 90, 100, 25);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(180, 90, 180, 28);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 90, 50), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(usernameField);

        // === Password ===
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passLabel.setBounds(80, 130, 100, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(180, 130, 180, 28);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 90, 50), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(passwordField);

        // === Role Selection ===
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        roleLabel.setBounds(80, 170, 100, 25);
        panel.add(roleLabel);

        String[] roles = {"customer", "admin"};
        roleBox = new JComboBox<>(roles);
        roleBox.setBounds(180, 170, 180, 28);
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(roleBox);

        // === Register Button ===
        registerButton = new JButton("Register");
        registerButton.setBounds(180, 220, 180, 35);
        registerButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        registerButton.setBackground(new Color(120, 70, 30));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(registerButton);

        // Hover effect
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(150, 90, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(120, 70, 30));
            }
        });

        registerButton.addActionListener(_ -> registerUser());

        // === Back Button ===
        backButton = new JButton("← Back to Login");
        backButton.setBounds(180, 265, 180, 28);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setBackground(new Color(200, 170, 130));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            dispose();
            new LoginForm();
        });
        panel.add(backButton);

        add(panel);
        setVisible(true);
    }

    // === REGISTER FUNCTION ===
    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check if username already exists
            String checkQuery = "SELECT * FROM users WHERE username=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insert new user
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful! You can now log in.");
                dispose();
                new LoginForm();
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterForm::new);
    }
}
