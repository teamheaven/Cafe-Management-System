import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class Dashboard extends JFrame {

    private JPanel sidebar, headerPanel;
    private JLabel titleLabel, welcomeLabel, taglineLabel, background;
    private JButton orderBtn, menuBtn, logoutBtn;
    private String userName;
    private Image backgroundImage;

    public Dashboard(String userName) {
        this.userName = userName;

        // === Frame setup ===
        setTitle("Cafe Management System - Cafe Delight");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("bg1_cafe.jpeg"));
        } catch (Exception e) {
            System.out.println("Background not found!");
        }

        // === Background with full-image overlay ===
        background = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                    // === Full image black overlay ===
                    Graphics2D g2d = (Graphics2D) g.create();
                    Color overlayColor = new Color(0, 0, 0, 150); // darker overlay (0–255 alpha)
                    g2d.setColor(overlayColor);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            }
        };
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setLayout(null);
        setContentPane(background);

        // === Header ===
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(92, 64, 51));
        headerPanel.setBounds(0, 0, 1000, 60);
        headerPanel.setLayout(null);
        background.add(headerPanel);

        titleLabel = new JLabel("Cafe Management System - Cafe Delight");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setBounds(30, 15, 450, 30);
        headerPanel.add(titleLabel);

        // === Sidebar ===
        sidebar = new JPanel();
        sidebar.setBackground(new Color(44, 29, 22, 230));
        sidebar.setBounds(0, 60, 230, 590);
        sidebar.setLayout(null);
        background.add(sidebar);

        orderBtn = createSidebarButton("🍽  New Order", 100);
        menuBtn = createSidebarButton("☕  Menu", 160);
        logoutBtn = createSidebarButton("🚪  Logout", 480);

        sidebar.add(orderBtn);
        sidebar.add(menuBtn);
        sidebar.add(logoutBtn);

        // === Load Custom Fonts ===
        Font titleFont, taglineFont;
        try {
            InputStream playfairStream = getClass().getResourceAsStream("/font/PlayfairDisplay-VariableFont_wght.ttf");
            InputStream poppinsStream = getClass().getResourceAsStream("/font/Poppins-Regular.ttf");

            titleFont = Font.createFont(Font.TRUETYPE_FONT, playfairStream).deriveFont(48f);
            taglineFont = Font.createFont(Font.TRUETYPE_FONT, poppinsStream).deriveFont(18f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(titleFont);
            ge.registerFont(taglineFont);
        } catch (Exception e) {
            System.out.println("Font not found, using defaults!");
            titleFont = new Font("Serif", Font.BOLD | Font.ITALIC, 40);
            taglineFont = new Font("SansSerif", Font.PLAIN, 16);
        }

        // === Center Welcome Section ===
        welcomeLabel = new JLabel("Welcome, " + userName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(titleFont);
        welcomeLabel.setForeground(new Color(255, 215, 0)); 
        welcomeLabel.setBounds(300, 220, 600, 60);
        background.add(welcomeLabel);

        taglineLabel = new JLabel("Select an option from the sidebar!", SwingConstants.CENTER);
        taglineLabel.setFont(taglineFont);
        taglineLabel.setForeground(new Color(240, 240, 240)); 
        taglineLabel.setBounds(300, 280, 600, 40);
        background.add(taglineLabel);

        // === Button Actions ===
        orderBtn.addActionListener(e -> {
            dispose();
            new OrderForm(userName, new ArrayList<>());
        });

        menuBtn.addActionListener(e -> {
            dispose();
            new menupage(userName);
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                background.repaint();
            }
        });

        setVisible(true);
    }

    private JButton createSidebarButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(20, y, 190, 40);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(92, 64, 51));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(115, 78, 60));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(92, 64, 51));
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        new Dashboard("admin");
    }
}
