package com.mycompany.coffee;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ReceiptWindow extends JFrame {

    public ReceiptWindow(int orderId, Map<Integer, UserDashboard.CartItem> items,
                         double total, double cash) {

        super("Receipt - Order #" + orderId);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Coffee Shop Receipt", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(10));

        // Order ID
        JLabel orderLabel = new JLabel("Order ID: " + orderId);
        orderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        orderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(orderLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(new JSeparator());

        mainPanel.add(Box.createVerticalStrut(10));

        // Items header (optional)
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("Item"));
        headerPanel.add(new JLabel("Qty", SwingConstants.CENTER));
        headerPanel.add(new JLabel("Subtotal", SwingConstants.RIGHT));
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(5));

        // List items with proper alignment
        for (UserDashboard.CartItem item : items.values()) {
            JPanel row = new JPanel(new GridLayout(1, 3));
            row.setOpaque(false);

            JLabel nameLabel = new JLabel(item.name);
            JLabel qtyLabel = new JLabel(String.valueOf(item.quantity), SwingConstants.CENTER);
            JLabel subtotalLabel = new JLabel("₱" + String.format("%.2f", item.price * item.quantity), SwingConstants.RIGHT);

            row.add(nameLabel);
            row.add(qtyLabel);
            row.add(subtotalLabel);

            mainPanel.add(row);
        }

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(new JSeparator());
        mainPanel.add(Box.createVerticalStrut(10));

        // Totals
        JPanel totalPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        totalPanel.setOpaque(false);

        totalPanel.add(new JLabel("Total:"));
        totalPanel.add(new JLabel("₱" + String.format("%.2f", total), SwingConstants.RIGHT));

        totalPanel.add(new JLabel("Cash:"));
        totalPanel.add(new JLabel("₱" + String.format("%.2f", cash), SwingConstants.RIGHT));

        double change = cash - total;
        totalPanel.add(new JLabel("Change:"));
        totalPanel.add(new JLabel("₱" + String.format("%.2f", change), SwingConstants.RIGHT));

        mainPanel.add(totalPanel);

        add(new JScrollPane(mainPanel));
        setVisible(true);
    }
}
