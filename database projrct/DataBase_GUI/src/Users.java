import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Users extends JFrame {
    private Connection connection;
    private JTable table;
    private JTextField searchField;
    private String userType;

    public Users(String userType) {
        this.userType = userType;
        setTitle("Users");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create a JPanel for search bar
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);

        JButton btnNewButton = new JButton("Menu");

        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle navigation to the main menu here
                dispose(); // Close the current window

                // Open the main menu page
                MainMenu mainMenu = new MainMenu(userType);
                mainMenu.setVisible(true);
                
                
                
            }
        });
        
        
        

        searchPanel.add(btnNewButton);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                searchUsers(query);
            } else {
                fetchUsers(); // If search query is empty, fetch all users
            }
        });
        searchPanel.add(searchButton);

        // Add button for changing user password (visible for administrators)
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> changePassword());
        changePasswordButton.setEnabled(userType.equals("Administrator")); // Enabled only for administrators
        searchPanel.add(changePasswordButton);


        
      
        
        // Add search panel to the frame
        getContentPane().add(searchPanel, BorderLayout.NORTH);

        // Create a new JTable
        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 12)); // Set font for table content
        table.setRowHeight(25); // Set row height
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14)); // Set font for table header

        // Set custom cell renderer for center-aligning cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Connect to the database and fetch users
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            fetchUsers();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // 1 row, 3 columns, with 10px horizontal and vertical gaps
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the buttons

        
        

        
        
     // Add button for adding a new user
        JButton addButton = new JButton("Add User");
        addButton.setEnabled(userType.equals("Administrator") || userType.equals("Librarian")); // Enabled for both administrators and librarians
        addButton.addActionListener(e -> addUser());
        buttonsPanel.add(addButton);


        
        
     // Add button for updating a user
        JButton updateButton = new JButton("Update User");
        updateButton.setEnabled(userType.equals("Administrator")); // Enabled only for administrators
        updateButton.addActionListener(e -> updateUser()); // Add action listener to perform update action
        buttonsPanel.add(updateButton);

        // Add button for deleting a user
        JButton deleteButton = new JButton("Delete User");
        deleteButton.setEnabled(userType.equals("Administrator")); // Enabled only for administrators
        deleteButton.addActionListener(e -> deleteSelectedUser()); // Add action listener to perform delete action
        buttonsPanel.add(deleteButton);

        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        // Make the frame visible after all components are added
        setVisible(true);
    }


    private void fetchUsers() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("CALL ListUsers()");

            // Create a DefaultTableModel to store users
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("UserID");
            model.addColumn("UserType");
            model.addColumn("UserName");
            model.addColumn("Password");

            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("UserID"),
                        resultSet.getString("UserType"),
                        resultSet.getString("UserName"),
                        resultSet.getString("Password")
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchUsers(String query) {
        try {
            String sql = "SELECT * FROM Users WHERE UserID LIKE ? OR UserType LIKE ? OR UserName LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= 3; i++) {
                preparedStatement.setString(i, "%" + query + "%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a DefaultTableModel to store search results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("UserID");
            model.addColumn("UserType");
            model.addColumn("UserName");
            model.addColumn("Password");

            // Populate the table model with search results
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("UserID"),
                        resultSet.getString("UserType"),
                        resultSet.getString("UserName"),
                        resultSet.getString("Password")
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userID = (int) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Prepare the SQL statement to call the stored procedure
                String sql = "{CALL DeleteUser(?)}";
                CallableStatement callableStatement = connection.prepareCall(sql);
                callableStatement.setInt(1, userID);

                // Execute the stored procedure
                callableStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                fetchUsers(); // Refresh the table after deleting the user
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to delete user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addUser() {
        // Gather input for new user
        String userType = JOptionPane.showInputDialog(this, "Enter User Type:");
        String userName = JOptionPane.showInputDialog(this, "Enter User Name:");
        String password = JOptionPane.showInputDialog(this, "Enter Password:");
        
        // Get the UserID from the user
        String userIDStr = JOptionPane.showInputDialog(this, "Enter User ID:");
        
        // Validate and convert the User ID to an integer
        int userID;
        try {
            userID = Integer.parseInt(userIDStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid User ID format. Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call stored procedure to add the user
        try {
            // Prepare the SQL statement to call the stored procedure
            String sql = "{CALL AddUser(?, ?, ?, ?)}";
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setInt(1, userID);
            callableStatement.setString(2, userType);
            callableStatement.setString(3, userName);
            callableStatement.setString(4, password);

            // Execute the stored procedure
            callableStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "User added successfully!");
            fetchUsers(); // Refresh the table after adding the user
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeUserPassword(int userID, String newPassword) {
        try {
            // Prepare the SQL statement to call the stored procedure
            String sql = "{CALL ChangeUserPassword(?, ?)}";
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setInt(1, userID);
            callableStatement.setString(2, newPassword);

            // Execute the stored procedure
            callableStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Password changed successfully!");
            fetchUsers(); // Refresh the table after changing password
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to change password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to change password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userID = (int) table.getValueAt(selectedRow, 0);
        String newPassword = JOptionPane.showInputDialog(this, "Enter New Password:");
        if (newPassword != null && !newPassword.isEmpty()) {
            // Call the method to change user's password
            changeUserPassword(userID, newPassword);
        }
    }

    private void updateUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Gather input for user update
        int userID = (int) table.getValueAt(selectedRow, 0);
        String userType = JOptionPane.showInputDialog(this, "Enter User Type:", table.getValueAt(selectedRow, 1));
        String userName = JOptionPane.showInputDialog(this, "Enter User Name:", table.getValueAt(selectedRow, 2));
        String password = JOptionPane.showInputDialog(this, "Enter Password:", table.getValueAt(selectedRow, 3));

        // Check if any of the input fields are null
        if (userType != null && userName != null && password != null) {
            // Call stored procedure to update the user
            try {
                // Prepare the SQL statement to call the stored procedure
                String sql = "{CALL UpdateUser(?, ?, ?, ?)}";
                CallableStatement callableStatement = connection.prepareCall(sql);
                callableStatement.setInt(1, userID);
                callableStatement.setString(2, userType);
                callableStatement.setString(3, userName);
                callableStatement.setString(4, password);

                // Execute the stored procedure
                callableStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "User updated successfully!");
                fetchUsers(); // Refresh the table after updating the user
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String userType = new Login().getUserTypeFromLogin(); // Retrieve user type from login
            new Users(userType);
        });
    }
}
