import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginForm() {
        setTitle("☕ Cafe Management System - Login");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // === Background Panel with Gradient ===
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 230, 200);
                Color color2 = new Color(180, 120, 80);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // === Title ===
        JLabel titleLabel = new JLabel("Cafe Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        titleLabel.setBounds(0, 20, 420, 40);
        titleLabel.setForeground(new Color(60, 30, 10));
        mainPanel.add(titleLabel);

        // === Username Field ===
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        userLabel.setBounds(70, 90, 100, 25);
        mainPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(160, 90, 180, 28);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 90, 50), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        mainPanel.add(usernameField);

        // === Password Field ===
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passLabel.setBounds(70, 130, 100, 25);
        mainPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(160, 130, 180, 28);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 90, 50), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        mainPanel.add(passwordField);

        // === Login Button ===
        loginButton = new JButton("Login");
        loginButton.setBounds(160, 180, 180, 35);
        loginButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        loginButton.setBackground(new Color(120, 70, 30));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(150, 90, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(120, 70, 30));
            }
        });

        loginButton.addActionListener(_ -> authenticateUser());
        mainPanel.add(loginButton);

        // === Register Button ===
        registerButton = new JButton("New User? Register Here");
        registerButton.setBounds(160, 225, 180, 25);
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerButton.setForeground(new Color(90, 50, 30));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        registerButton.addActionListener(e -> {
            dispose();
            new RegisterForm(); // open registration form
        });
        mainPanel.add(registerButton);

        // === Footer ===
        JLabel footer = new JLabel("© 2025 Cafe Management System - DSN", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(90, 60, 40));
        footer.setBounds(0, 280, 420, 20);
        mainPanel.add(footer);

        add(mainPanel);
        setVisible(true);
    }

    private void authenticateUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "Login Successful!");

                dispose();
                if ("admin".equalsIgnoreCase(role)) {
                    new OrderHistoryPage().setVisible(true); // admin page
                } else {
                    new Dashboard(username).setVisible(true); // user dashboard
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
