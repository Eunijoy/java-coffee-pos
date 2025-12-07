package com.mycompany.coffee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewOrdersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel salesLabel;  // Total sales display

    public ViewOrdersPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("View Orders");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Table columns
        String[] columns = {"Order ID", "Customer", "Total (₱)", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Sales panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        salesLabel = new JLabel("Total Sales: ₱0.00");
        salesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(salesLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load initial data
        loadOrders();
        loadTotalSales();
    }

    /* -------------------------------------------------------------
     * LOAD ORDERS (Aligned with your SQL database)
     * ------------------------------------------------------------- */
    private void loadOrders() {
        String sql = "SELECT o.order_id, u.username, o.total_amount, o.order_status " +
                     "FROM orders o " +
                     "LEFT JOIN users u ON o.user_id = u.user_id " +
                     "ORDER BY o.order_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String customer = rs.getString("username");
                double total = rs.getDouble("total_amount");
                String status = rs.getString("order_status");

                Object[] row = {orderId, customer, String.format("%.2f", total), status};
                tableModel.addRow(row);
            }

            logActivity("LOAD_ORDERS", "Order table refreshed from database");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    /* -------------------------------------------------------------
     * LOAD TOTAL SALES (Only completed orders)
     * ------------------------------------------------------------- */
    private void loadTotalSales() {
        String sql = "SELECT SUM(total_amount) AS sales " +
                     "FROM orders WHERE order_status = 'completed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                double sales = rs.getDouble("sales");
                salesLabel.setText("Total Sales: ₱" + String.format("%.2f", sales));

                logActivity("LOAD_SALES", "Sales calculated: ₱" + sales);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading sales: " + e.getMessage());
        }
    }


    /* -------------------------------------------------------------
     * LOG ACTIVITY (Fully aligns with SQL table structure)
     * ------------------------------------------------------------- */
    private void logActivity(String activity, String details) {

        String sql = "INSERT INTO logs (activity, details) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, activity);
            pstmt.setString(2, details);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Log error: " + e.getMessage());
        }
    }

    /* -------------------------------------------------------------
     * Refresh method for external calls
     * ------------------------------------------------------------- */
    public void refreshOrders() {
        loadOrders();
        loadTotalSales();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Orders");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.add(new ViewOrdersPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
