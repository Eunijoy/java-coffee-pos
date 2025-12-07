package com.mycompany.coffee;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton userLoginButton, adminLoginButton;
    private JPanel mainPanel, loginPanel, titlePanel;
    private JLabel titleLabel;

    public LoginGUI() {
        super("Coffee Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);

        // Coffee color scheme
        Color darkBrown = new Color(101, 67, 33);
        Color mediumBrown = new Color(139, 90, 43);
        Color lightBrown = new Color(205, 133, 63);
        Color cream = new Color(245, 222, 179);
        Color white = Color.WHITE;

        // Main Panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(cream);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(darkBrown);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        titleLabel = new JLabel("Coffee Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(cream);
        titlePanel.add(titleLabel);

        // Login Panel
        loginPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        loginPanel.setBackground(cream);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(darkBrown);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(darkBrown);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Buttons
        userLoginButton = new JButton("Login as User");
        styleButton(userLoginButton, lightBrown, white);

        adminLoginButton = new JButton("Login as Admin");
        styleButton(adminLoginButton, darkBrown, white);

        // Add login panel components
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(userLoginButton);
        loginPanel.add(adminLoginButton);

        // Combine into main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // Button Actions
        userLoginButton.addActionListener(e -> userLogin());
        adminLoginButton.addActionListener(e -> adminLogin());
    }

    // Reusable style for buttons
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
    }

    private void userLogin() {
        loginWithRole("user");
    }

    private void adminLogin() {
        loginWithRole("admin");
    }

    /**
     * Authenticate user/admin.
     * Returns user_id if success, -1 if failed.
     */
    private int authenticateUser(String username, String password, String role) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database not connected!");
                return -1;
            }

            String sql = "SELECT user_id FROM users WHERE username = ? AND password = ? AND role = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("user_id");
                    } else {
                        return -1;
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage());
            return -1;
        }
    }

    private void loginWithRole(String role) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        int userId = authenticateUser(username, password, role);

        if (userId != -1) {
            if (role.equals("admin")) {
                JOptionPane.showMessageDialog(this, "Admin login successful!");
                dispose();
                SwingUtilities.invokeLater(() -> new AdminDashboard());
            } else {
                JOptionPane.showMessageDialog(this, "User login successful!");
                dispose(); // close login
                SwingUtilities.invokeLater(() -> new UserDashboard(userId)); // pass user_id
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}
