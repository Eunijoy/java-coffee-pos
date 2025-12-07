package com.mycompany.coffee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageCoffeePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public ManageCoffeePanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Coffee");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Table model including Description and Image Path
        model = new DefaultTableModel(new Object[]{
                "ID", "Name", "Description", "Price", "Stock", "Category", "Image Path"
        }, 0);
        table = new JTable(model);

        // Buttons
        JButton addBtn = new JButton("Add Coffee");
        JButton editBtn = new JButton("Edit Coffee");
        JButton deleteBtn = new JButton("Delete Coffee");

        addBtn.addActionListener(e -> addCoffee());
        editBtn.addActionListener(e -> editCoffee());
        deleteBtn.addActionListener(e -> deleteCoffee());

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loadCoffeeData(); // Load database data
    }

    // -----------------------------
    // LOAD COFFEE DATA FROM SQL
    // -----------------------------
    private void loadCoffeeData() {
        model.setRowCount(0); // Clear table

        try {
            Connection con = DatabaseConnection.getConnection();
            String query = "SELECT coffee_id, coffee_name, description, price, stock_quantity, category, image_path FROM coffee_menu";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("coffee_id"),
                        rs.getString("coffee_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getString("category"),
                        rs.getString("image_path")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading coffee list: " + e.getMessage());
        }
    }

    // -----------------------------
    // ADD COFFEE
    // -----------------------------
    private void addCoffee() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField imageField = new JTextField();

        Object[] form = {
                "Coffee Name:", nameField,
                "Description:", descField,
                "Price:", priceField,
                "Stock Quantity:", stockField,
                "Category:", categoryField,
                "Image Path:", imageField
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Add Coffee", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Connection con = DatabaseConnection.getConnection();
                String query = "INSERT INTO coffee_menu (coffee_name, description, price, stock_quantity, category, image_path) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);

                ps.setString(1, nameField.getText());
                ps.setString(2, descField.getText());
                ps.setDouble(3, Double.parseDouble(priceField.getText()));
                ps.setInt(4, Integer.parseInt(stockField.getText()));
                ps.setString(5, categoryField.getText());
                ps.setString(6, imageField.getText());

                ps.executeUpdate();
                loadCoffeeData();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding coffee: " + e.getMessage());
            }
        }
    }

    // -----------------------------
    // EDIT COFFEE
    // -----------------------------
    private void editCoffee() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a coffee item to edit.");
            return;
        }

        int id = Integer.parseInt(getStringValue(model.getValueAt(row, 0)));

        JTextField nameField = new JTextField(getStringValue(model.getValueAt(row, 1)));
        JTextField descField = new JTextField(getStringValue(model.getValueAt(row, 2)));
        JTextField priceField = new JTextField(getStringValue(model.getValueAt(row, 3)));
        JTextField stockField = new JTextField(getStringValue(model.getValueAt(row, 4)));
        JTextField categoryField = new JTextField(getStringValue(model.getValueAt(row, 5)));
        JTextField imageField = new JTextField(getStringValue(model.getValueAt(row, 6)));

        Object[] form = {
                "Coffee Name:", nameField,
                "Description:", descField,
                "Price:", priceField,
                "Stock Quantity:", stockField,
                "Category:", categoryField,
                "Image Path:", imageField
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Coffee", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Connection con = DatabaseConnection.getConnection();
                String query = "UPDATE coffee_menu SET coffee_name=?, description=?, price=?, stock_quantity=?, category=?, image_path=? WHERE coffee_id=?";
                PreparedStatement ps = con.prepareStatement(query);

                ps.setString(1, nameField.getText());
                ps.setString(2, descField.getText());
                ps.setDouble(3, Double.parseDouble(priceField.getText()));
                ps.setInt(4, Integer.parseInt(stockField.getText()));
                ps.setString(5, categoryField.getText());
                ps.setString(6, imageField.getText());
                ps.setInt(7, id);

                ps.executeUpdate();
                loadCoffeeData();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating coffee: " + e.getMessage());
            }
        }
    }

    // -----------------------------
    // DELETE COFFEE
    // -----------------------------
    private void deleteCoffee() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a coffee item to delete.");
            return;
        }

        int id = Integer.parseInt(getStringValue(model.getValueAt(row, 0)));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this coffee?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection con = DatabaseConnection.getConnection();
                String query = "DELETE FROM coffee_menu WHERE coffee_id=?";
                PreparedStatement ps = con.prepareStatement(query);

                ps.setInt(1, id);
                ps.executeUpdate();
                loadCoffeeData();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting coffee: " + e.getMessage());
            }
        }
    }

    // Helper method to avoid nulls
    private String getStringValue(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
