import javax.swing.*;
import java.awt.event.*;

public class Dashboard extends JFrame {
    String username;

    public Dashboard(String username) {
        this.username = username;

        setTitle("Cafe Dashboard");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setBounds(130, 30, 200, 30);
        add(welcomeLabel);

        JButton orderBtn = new JButton("Take Order");
        orderBtn.setBounds(120, 80, 150, 40);
        add(orderBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(120, 140, 150, 40);
        add(logoutBtn);

        orderBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OrderForm();  // open Order Form
                dispose();
            }
        });

        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginForm();  // go back to Login
                dispose();
            }
        });

        setVisible(true);
    }

    // You can also call this directly to test
    public static void main(String[] args) {
        new Dashboard("TestUser");
    }
}
