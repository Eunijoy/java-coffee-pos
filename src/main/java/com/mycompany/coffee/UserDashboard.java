package com.mycompany.coffee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class UserDashboard extends JFrame {

    // Coffee color theme
    private final Color darkBrown = new Color(101, 67, 33);
    private final Color mediumBrown = new Color(139, 90, 43);
    private final Color lightBrown = new Color(205, 133, 63);
    private final Color cream = new Color(245, 222, 179);
    private final Color white = Color.WHITE;

    private JPanel mainPanel, coffeePanel, cartPanel;
    private JLabel totalLabel;

    private double totalAmount = 0.0;
    private final int userId;

    // Cart storage
    private final Map<Integer, CartItem> cartItems = new LinkedHashMap<>();

    public UserDashboard(int userId) {
        super("Coffee Management System - User Menu");
        this.userId = userId;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(cream);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Coffee menu grid
        coffeePanel = new JPanel(new GridLayout(0, 3, 10, 10));
        coffeePanel.setBackground(cream);

        JScrollPane coffeeScroll = new JScrollPane(coffeePanel);
        coffeeScroll.setBorder(BorderFactory.createTitledBorder("Coffee Menu"));

        // Cart panel
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(cream);
        cartPanel.setBorder(BorderFactory.createTitledBorder("Your Cart"));

        totalLabel = new JLabel("Total: ₱0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(darkBrown);

        JButton clearBtn = new JButton("Clear Cart");
        styleButton(clearBtn, mediumBrown, white);
        clearBtn.addActionListener(e -> clearCart());

        JButton checkoutBtn = new JButton("Checkout");
        styleButton(checkoutBtn, darkBrown, white);
        checkoutBtn.addActionListener(e -> checkout());

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, Color.RED, white);
        logoutBtn.addActionListener(e -> logout());

        cartPanel.add(totalLabel);
        cartPanel.add(Box.createVerticalStrut(10));
        cartPanel.add(clearBtn);
        cartPanel.add(Box.createVerticalStrut(10));
        cartPanel.add(checkoutBtn);
        cartPanel.add(Box.createVerticalStrut(10));
        cartPanel.add(logoutBtn);

        mainPanel.add(coffeeScroll, BorderLayout.CENTER);
        mainPanel.add(cartPanel, BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);

        loadCoffeeMenu();
    }

    // Load coffee menu
    private void loadCoffeeMenu() {
        String sql = "SELECT * FROM coffee_menu WHERE is_available = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("coffee_id");
                String name = rs.getString("coffee_name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("image_path");
                int stock = rs.getInt("stock_quantity");

                coffeePanel.add(createCoffeeCard(id, name, description, price, imagePath, stock));
            }
            coffeePanel.revalidate();
            coffeePanel.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading coffee menu: " + e.getMessage());
        }
    }

    // Create coffee card UI (with description)
    private JPanel createCoffeeCard(int id, String name, String description, double price, String imagePath, int stock) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cream);
        panel.setBorder(BorderFactory.createLineBorder(darkBrown, 2));

        // Coffee image
        String basePath = "C:/Users/Administrator/Documents/NetBeansProjects/coffee/";
        ImageIcon icon;
        try {
            icon = new ImageIcon(basePath + imagePath);
            if (icon.getIconWidth() <= 0) throw new Exception();
        } catch (Exception e) {
            icon = new ImageIcon(new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB));
        }
        Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(img), SwingConstants.CENTER);

        // Name label
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(darkBrown);

        // Description label
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        descLabel.setForeground(darkBrown);

        // Price label
        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", price), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        priceLabel.setForeground(darkBrown);

        // Add to Cart button
        JButton addBtn = new JButton(stock > 0 ? "Add to Cart" : "OUT OF STOCK");
        styleButton(addBtn, stock > 0 ? mediumBrown : Color.GRAY, white);

        if (stock > 0) {
            addBtn.addActionListener(e -> addToCart(id, name, price));
        } else {
            addBtn.setEnabled(false);
        }

        // Bottom panel for price + button
        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.setBackground(cream);
        bottom.add(priceLabel);
        bottom.add(addBtn);

        // Center panel to hold name + description
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(cream);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(nameLabel);
        centerPanel.add(descLabel);

        panel.add(imgLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    // Add to cart
    private void addToCart(int id, String name, double price) {
        if (cartItems.containsKey(id)) {
            cartItems.get(id).quantity++;
        } else {
            cartItems.put(id, new CartItem(id, name, price, 1));
        }
        updateCartDisplay();
    }

    // Update cart layout
    private void updateCartDisplay() {
        cartPanel.removeAll();

        for (CartItem item : cartItems.values()) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBackground(cream);

            JLabel label = new JLabel(item.name + " - ₱" + item.price);
            label.setForeground(darkBrown);

            JButton minus = new JButton(" - ");
            JButton plus = new JButton(" + ");
            JButton remove = new JButton("Remove");

            styleSmallButton(minus);
            styleSmallButton(plus);
            styleSmallButton(remove);

            minus.addActionListener(e -> {
                if (item.quantity > 1) item.quantity--;
                else cartItems.remove(item.id);
                updateCartDisplay();
            });

            plus.addActionListener(e -> {
                item.quantity++;
                updateCartDisplay();
            });

            remove.addActionListener(e -> {
                cartItems.remove(item.id);
                updateCartDisplay();
            });

            row.add(label);
            row.add(minus);
            row.add(new JLabel(" " + item.quantity + " "));
            row.add(plus);
            row.add(remove);

            cartPanel.add(row);
        }

        totalAmount = cartItems.values().stream()
                .mapToDouble(i -> i.price * i.quantity)
                .sum();

        totalLabel.setText("Total: ₱" + String.format("%.2f", totalAmount));
        cartPanel.add(Box.createVerticalStrut(10));

        JButton clearBtn = new JButton("Clear Cart");
        styleButton(clearBtn, mediumBrown, white);
        clearBtn.addActionListener(e -> clearCart());

        JButton checkoutBtn = new JButton("Checkout");
        styleButton(checkoutBtn, darkBrown, white);
        checkoutBtn.addActionListener(e -> checkout());

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, Color.RED, white);
        logoutBtn.addActionListener(e -> logout());

        cartPanel.add(clearBtn);
        cartPanel.add(Box.createVerticalStrut(10));
        cartPanel.add(checkoutBtn);
        cartPanel.add(Box.createVerticalStrut(10));
        cartPanel.add(logoutBtn);

        cartPanel.revalidate();
        cartPanel.repaint();
    }

    // Clear cart
    private void clearCart() {
        cartItems.clear();
        updateCartDisplay();
    }

    // Checkout
    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        String cashStr = JOptionPane.showInputDialog(this, "Enter cash amount:");
        if (cashStr == null) return;

        double cash;
        try {
            cash = Double.parseDouble(cashStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid cash amount!");
            return;
        }

        if (cash < totalAmount) {
            JOptionPane.showMessageDialog(this, "Not enough cash!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO orders (user_id, total_amount, order_status) VALUES (?, ?, 'completed')";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, totalAmount);
            orderStmt.executeUpdate();

            ResultSet keys = orderStmt.getGeneratedKeys();
            keys.next();
            int orderId = keys.getInt(1);

            String itemSql = "INSERT INTO order_items (order_id, coffee_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);

            String stockSql = "UPDATE coffee_menu SET stock_quantity = stock_quantity - ? WHERE coffee_id = ?";
            PreparedStatement stockStmt = conn.prepareStatement(stockSql);

            for (CartItem item : cartItems.values()) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.id);
                itemStmt.setInt(3, item.quantity);
                itemStmt.setDouble(4, item.price);
                itemStmt.setDouble(5, item.price * item.quantity);
                itemStmt.addBatch();

                stockStmt.setInt(1, item.quantity);
                stockStmt.setInt(2, item.id);
                stockStmt.addBatch();
            }

            itemStmt.executeBatch();
            stockStmt.executeBatch();
            conn.commit();

            new ReceiptWindow(orderId, cartItems, totalAmount, cash);

            cartItems.clear();
            updateCartDisplay();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Checkout error: " + e.getMessage());
        }
    }

    // Logout and redirect to LoginGUI
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginGUI().setVisible(true); // Redirect to LoginGUI
        }
    }

    // Button styles
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSmallButton(JButton btn) {
        btn.setBackground(lightBrown);
        btn.setForeground(white);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
    }

    // CartItem class
    public static class CartItem {
        public int id;
        public String name;
        public double price;
        public int quantity;

        public CartItem(int id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard(2));
    }
}
