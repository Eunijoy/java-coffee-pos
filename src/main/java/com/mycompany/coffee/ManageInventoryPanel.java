/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.coffee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageInventoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public ManageInventoryPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Inventory");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        model = new DefaultTableModel(new String[]{"ID", "Coffee Name", "Stock", "Category"}, 0);
        table = new JTable(model);

        JButton restockBtn = new JButton("Restock");
        JButton deductBtn = new JButton("Deduct Stock");
        JButton updateBtn = new JButton("Set Stock");

        restockBtn.addActionListener(e -> restock());
        deductBtn.addActionListener(e -> deductStock());
        updateBtn.addActionListener(e -> updateStock());

        JPanel btnPanel = new JPanel();
        btnPanel.add(restockBtn);
        btnPanel.add(deductBtn);
        btnPanel.add(updateBtn);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loadInventory();
    }

    // LOAD INVENTORY
    private void loadInventory() {
        model.setRowCount(0);

        try {
            Connection con = DatabaseConnection.getConnection();
            String sql = "SELECT coffee_id, coffee_name, stock_quantity, category FROM coffee_menu";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("coffee_id"),
                        rs.getString("coffee_name"),
                        rs.getInt("stock_quantity"),
                        rs.getString("category")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading inventory: " + e.getMessage());
        }
    }

    // RESTOCK
    private void restock() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a coffee item first.");
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to add:");
        if (qtyStr == null) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE coffee_menu SET stock_quantity = stock_quantity + ? WHERE coffee_id=?"
            );

            ps.setInt(1, qty);
            ps.setInt(2, id);
            ps.executeUpdate();

            loadInventory();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error restocking: " + e.getMessage());
        }
    }

    // DEDUCT STOCK
    private void deductStock() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to deduct:");
        if (qtyStr == null) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE coffee_menu SET stock_quantity = stock_quantity - ? WHERE coffee_id=?"
            );

            ps.setInt(1, qty);
            ps.setInt(2, id);
            ps.executeUpdate();

            loadInventory();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deducting stock: " + e.getMessage());
        }
    }

    // SET STOCK DIRECTLY
    private void updateStock() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "Set new stock quantity:");
        if (qtyStr == null) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE coffee_menu SET stock_quantity = ? WHERE coffee_id=?"
            );

            ps.setInt(1, qty);
            ps.setInt(2, id);

            ps.executeUpdate();
            loadInventory();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating stock: " + e.getMessage());
        }
    }
}
