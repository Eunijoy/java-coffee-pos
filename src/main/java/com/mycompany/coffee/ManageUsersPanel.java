package com.mycompany.coffee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageUsersPanel extends JPanel {

    private JTable table;
    private String[] columns = {"User ID", "Username", "Role"};
    private DefaultTableModel tableModel;

    public ManageUsersPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Users");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Initialize table model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        loadUserData(); // Load from DB

        JButton addBtn = new JButton("Add User");
        JButton editBtn = new JButton("Edit User");
        JButton deleteBtn = new JButton("Delete User");

        addBtn.addActionListener(e -> openAddUserDialog());
        editBtn.addActionListener(e -> openEditUserDialog());
        deleteBtn.addActionListener(e -> deleteUser());

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        tableModel.setRowCount(0); // clear existing rows
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, username, role FROM users")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users from database.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddUserDialog() {
        UserDialog dialog = new UserDialog(null);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadUserData();
        }
    }

    private void openEditUserDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT username, full_name, email, role FROM users WHERE user_id = ?")) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserDialog dialog = new UserDialog(userId,
                        rs.getString("username"),
                        null, // password left empty
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("role")
                );
                dialog.setVisible(true);
                if (dialog.isSucceeded()) {
                    loadUserData();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading user details.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user '" + username + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {

            ps.setInt(1, userId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "User deleted successfully.");
            loadUserData();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting user.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inner class for Add/Edit User dialog
    private class UserDialog extends JDialog {
        private JTextField usernameField = new JTextField(20);
        private JPasswordField passwordField = new JPasswordField(20);
        private JTextField fullNameField = new JTextField(20);
        private JTextField emailField = new JTextField(20);
        private JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "user"});
        private JButton saveBtn = new JButton("Save");
        private JButton cancelBtn = new JButton("Cancel");

        private boolean succeeded = false;
        private Integer userId; // null for add, not null for edit

        public UserDialog(Integer userId) {
            this(userId, "", "", "", "", "user");
        }

        public UserDialog(Integer userId, String username, String password, String fullName, String email, String role) {
            super((Frame) null, userId == null ? "Add User" : "Edit User", true);
            this.userId = userId;

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints cs = new GridBagConstraints();
            cs.fill = GridBagConstraints.HORIZONTAL;

            cs.gridx = 0; cs.gridy = 0; cs.gridwidth = 1;
            panel.add(new JLabel("Username: "), cs);
            cs.gridx = 1; cs.gridy = 0; cs.gridwidth = 2;
            usernameField.setText(username);
            panel.add(usernameField, cs);

            cs.gridx = 0; cs.gridy = 1; cs.gridwidth = 1;
            panel.add(new JLabel("Password: "), cs);
            cs.gridx = 1; cs.gridy = 1; cs.gridwidth = 2;
            passwordField.setText(password);
            panel.add(passwordField, cs);

            cs.gridx = 0; cs.gridy = 2; cs.gridwidth = 1;
            panel.add(new JLabel("Full Name: "), cs);
            cs.gridx = 1; cs.gridy = 2; cs.gridwidth = 2;
            fullNameField.setText(fullName);
            panel.add(fullNameField, cs);

            cs.gridx = 0; cs.gridy = 3; cs.gridwidth = 1;
            panel.add(new JLabel("Email: "), cs);
            cs.gridx = 1; cs.gridy = 3; cs.gridwidth = 2;
            emailField.setText(email);
            panel.add(emailField, cs);

            cs.gridx = 0; cs.gridy = 4; cs.gridwidth = 1;
            panel.add(new JLabel("Role: "), cs);
            cs.gridx = 1; cs.gridy = 4; cs.gridwidth = 2;
            roleCombo.setSelectedItem(role);
            panel.add(roleCombo, cs);

            JPanel bp = new JPanel();
            bp.add(saveBtn);
            bp.add(cancelBtn);

            saveBtn.addActionListener(e -> saveUser());
            cancelBtn.addActionListener(e -> {
                succeeded = false;
                dispose();
            });

            getContentPane().add(panel, BorderLayout.CENTER);
            getContentPane().add(bp, BorderLayout.PAGE_END);

            pack();
            setResizable(false);
            setLocationRelativeTo(null);
        }

        private void saveUser() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || fullName.isEmpty() || role.isEmpty() || (userId == null && password.isEmpty())) {
                JOptionPane.showMessageDialog(UserDialog.this, "Please fill in all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                if (userId == null) {
                    // Add new user
                    String sql = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, username);
                        ps.setString(2, password); // consider hashing in production
                        ps.setString(3, fullName);
                        ps.setString(4, email.isEmpty() ? null : email);
                        ps.setString(5, role);
                        ps.executeUpdate();
                    }
                } else {
                    // Update existing user
                    String sql;
                    if (password.isEmpty()) {
                        sql = "UPDATE users SET username=?, full_name=?, email=?, role=? WHERE user_id=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, username);
                            ps.setString(2, fullName);
                            ps.setString(3, email.isEmpty() ? null : email);
                            ps.setString(4, role);
                            ps.setInt(5, userId);
                            ps.executeUpdate();
                        }
                    } else {
                        sql = "UPDATE users SET username=?, password=?, full_name=?, email=?, role=? WHERE user_id=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, username);
                            ps.setString(2, password); // consider hashing
                            ps.setString(3, fullName);
                            ps.setString(4, email.isEmpty() ? null : email);
                            ps.setString(5, role);
                            ps.setInt(6, userId);
                            ps.executeUpdate();
                        }
                    }
                }

                succeeded = true;
                dispose();

            } catch (SQLIntegrityConstraintViolationException e) {
                JOptionPane.showMessageDialog(UserDialog.this, "Username already exists!", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(UserDialog.this, "Database error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isSucceeded() {
            return succeeded;
        }
    }
}
