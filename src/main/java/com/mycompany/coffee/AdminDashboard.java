package com.mycompany.coffee;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JPanel mainPanel, sidebarPanel, contentPanel;
    private JButton manageCoffeeButton, manageUsersButton, manageInventoryButton, viewOrdersButton, logoutButton;
    private JLabel titleLabel;

    // Coffee color scheme
    Color darkBrown = new Color(101, 67, 33);
    Color mediumBrown = new Color(139, 90, 43);
    Color lightBrown = new Color(205, 133, 63);
    Color cream = new Color(245, 222, 179);
    Color white = Color.WHITE;

    // Panels we will load
    private ManageCoffeePanel manageCoffeePanel;
    private ManageUsersPanel manageUsersPanel;
    private ManageInventoryPanel manageInventoryPanel;
    private ViewOrdersPanel viewOrdersPanel;

    public AdminDashboard() {
        super("Coffee Management System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Initialize panels
        manageCoffeePanel = new ManageCoffeePanel();
        manageUsersPanel = new ManageUsersPanel();
        manageInventoryPanel = new ManageInventoryPanel();
        viewOrdersPanel = new ViewOrdersPanel();

        // Main Panel
        mainPanel = new JPanel(new BorderLayout());

        // Sidebar Panel
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(darkBrown);
        sidebarPanel.setPreferredSize(new Dimension(250, 600));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Title Label
        titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(cream);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Sidebar Buttons
        manageCoffeeButton = createSidebarButton("Manage Coffee");
        manageUsersButton = createSidebarButton("Manage Users");
        manageInventoryButton = createSidebarButton("Manage Inventory");
        viewOrdersButton = createSidebarButton("View Orders");
        logoutButton = createSidebarButton("Logout");

        sidebarPanel.add(titleLabel);
        sidebarPanel.add(manageCoffeeButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(manageUsersButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(manageInventoryButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(viewOrdersButton);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(logoutButton);

        // Content Panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cream);

        JLabel welcomeLabel = new JLabel("Welcome to Admin Dashboard!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(darkBrown);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Add to main panel
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // Button Listeners
        manageCoffeeButton.addActionListener(e -> switchPanel(manageCoffeePanel));
        manageUsersButton.addActionListener(e -> switchPanel(manageUsersPanel));
        manageInventoryButton.addActionListener(e -> switchPanel(manageInventoryPanel));
        viewOrdersButton.addActionListener(e -> switchPanel(viewOrdersPanel));
        logoutButton.addActionListener(e -> logout());
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(mediumBrown);
        button.setForeground(white);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(lightBrown);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(mediumBrown);
            }
        });

        return button;
    }

    private void switchPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginGUI();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
