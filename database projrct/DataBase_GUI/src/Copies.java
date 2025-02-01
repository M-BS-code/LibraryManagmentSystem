import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Copies extends JFrame {
    private Connection connection;
    private JTable table;
    private JTextField searchField;
    private String userType;

    public Copies(String userType) {
        this.userType = userType;
        setTitle("Copies Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create a JPanel for search bar
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        
                // Add a button for returning to the main menu
        JButton btnNewButton = new JButton("Menu");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle navigation to the main menu here
                dispose(); // Close the current window

                // Open the main menu page
                String userType = new Login().getUserTypeFromLogin(); // Retrieve user type from login
                MainMenu mainMenu = new MainMenu(userType);
                mainMenu.setVisible(true);
            }
        });
         searchPanel.add(btnNewButton); // Adding the menu button
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                searchCopies(query);
            } else {
                fetchCopies(); // If search query is empty, fetch all copies
            }
        });

        searchPanel.add(searchButton);

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

        // Create a JPanel for the buttons at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Align buttons to the center

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        JButton addCopyButton = new JButton("Add Copy");
        buttonPanel.add(addCopyButton);
        if (userType.equals("Administrator") || userType.equals("Librarian")) {
            addCopyButton.addActionListener(e -> addCopy());
        }

        // Add button to delete copy
        JButton deleteCopyButton = new JButton("Delete Copy");
        buttonPanel.add(deleteCopyButton);
        if (userType.equals("Administrator")) {
            deleteCopyButton.addActionListener(e -> deleteCopy());
        }

        // Add button to checkout copy
        JButton checkoutCopyButton = new JButton("Checkout Copy");
        buttonPanel.add(checkoutCopyButton);
        if (userType.equals("Administrator") || userType.equals("Student")) {
            checkoutCopyButton.addActionListener(e -> checkoutCopy());
        }

        // Connect to the database and fetch copies
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            fetchCopies();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Make the frame visible after all components are added
        setVisible(true);
    }
 
    private void fetchCopies() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Copies");

            // Create a DefaultTableModel to store copies
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("CopyID");
            model.addColumn("ResourceID");
            model.addColumn("Barcode");
            model.addColumn("Price");
            model.addColumn("PurchaseDate");
            model.addColumn("RackNumber");
            model.addColumn("NumberOfCopies");


            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("CopyID"),
                        resultSet.getInt("ResourceID"),
                        resultSet.getString("Barcode"),
                        resultSet.getDouble("Price"),
                        resultSet.getDate("PurchaseDate"),
                        resultSet.getInt("RackNumber"),
                        resultSet.getInt("NumberOfCopies")

 
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchCopies(String query) {
        try {
            String sql = "SELECT * FROM Copies WHERE CopyID LIKE ? OR Barcode LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + query + "%");
            preparedStatement.setString(2, "%" + query + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a DefaultTableModel to store search results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("CopyID");
            model.addColumn("ResourceID");
            model.addColumn("Barcode");
            model.addColumn("Price");
            model.addColumn("PurchaseDate");
            model.addColumn("RackNumber");
            model.addColumn("NumberOfCopies");


            // Populate the table model with search results
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("CopyID"),
                        resultSet.getInt("ResourceID"),
                        resultSet.getString("Barcode"),
                        resultSet.getDouble("Price"),
                        resultSet.getDate("PurchaseDate"),
                        resultSet.getInt("RackNumber"),
                        resultSet.getInt("NumberOfCopies")

                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addCopy() {
        try {
            // Get the maximum CopyID from the database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(CopyID) FROM Copies");
            int nextCopyID = 1; // Default to 1 if no copies exist yet
            if (resultSet.next()) {
                nextCopyID = resultSet.getInt(1) + 1;
            }

            // Gather input for new copy
            int resourceID = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Resource ID:"));
            String barcode = JOptionPane.showInputDialog(this, "Enter Barcode:");
            double price = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter Price:"));
            Date purchaseDate = Date.valueOf(JOptionPane.showInputDialog(this, "Enter Purchase Date (yyyy-MM-dd):"));
            int rackNumber = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Rack Number:"));
            int numberOfCopies = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Number of Copies:")); // Prompt for number of copies

            // Call stored procedure to add the copy
            CallableStatement callableStatement = connection.prepareCall("{CALL AddCopy(?, ?, ?, ?, ?, ?, ?)}");
            callableStatement.setInt(1, nextCopyID); // Use the next available CopyID
            callableStatement.setInt(2, resourceID);
            callableStatement.setString(3, barcode);
            callableStatement.setDouble(4, price);
            callableStatement.setDate(5, purchaseDate);
            callableStatement.setInt(6, rackNumber);
            callableStatement.setInt(7, numberOfCopies); // Pass the number of copies parameter
            callableStatement.execute();
            JOptionPane.showMessageDialog(this, "Copy added successfully!");
            fetchCopies(); // Refresh the table after adding the copy
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add copy: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deleteCopy() {
        // Prompt for the CopyID to delete
        int copyID = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Copy ID to delete:"));

        try {
            CallableStatement callableStatement = connection.prepareCall("{CALL DeleteCopy(?)}");
            callableStatement.setInt(1, copyID);
            callableStatement.execute();
            JOptionPane.showMessageDialog(this, "Copy deleted successfully!");
            fetchCopies(); // Refresh the table after deleting the copy
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete copy: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void checkoutCopy() {
        try {
            // Gather input for checking out copy
            int copyID = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Copy ID to checkout:"));
            int studentID = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Student ID:"));
            Date issueDate = Date.valueOf(JOptionPane.showInputDialog(this, "Enter Issue Date (yyyy-MM-dd):"));
            Date dueDate = Date.valueOf(JOptionPane.showInputDialog(this, "Enter Due Date (yyyy-MM-dd):"));

            // Check if the copy is available for checkout
            int availableCopies = getAvailableCopies(copyID);
            if (availableCopies > 0) {
                // Call method to checkout copy
                checkoutCopy(copyID, studentID, issueDate, dueDate);
            } else {
                JOptionPane.showMessageDialog(this, "Sorry, there are no available copies of this resource at the moment. Please try again later.", "No Available Copies", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter numeric values for Copy ID and Student ID.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Please enter dates in the format yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getAvailableCopies(int copyID) {
        try {
            // Prepare a SQL statement to check the number of copies available
            String sql = "SELECT NumberOfCopies FROM Copies WHERE CopyID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, copyID);
            
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Check if there are available copies
            if (resultSet.next()) {
                return resultSet.getInt("NumberOfCopies");
            } else {
                return 0; // Return 0 if the copy ID does not exist
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0; // Return 0 if an error occurs
        }
    }

    private void checkoutCopy(int copyID, int studentID, Date issueDate, Date dueDate) {
        try {
            // Calculate the difference in days between issueDate and dueDate
            long diffInMillies = Math.abs(dueDate.getTime() - issueDate.getTime());
            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

            // Check if the borrow period exceeds 15 days
            if (diffInDays > 15) {
                JOptionPane.showMessageDialog(this, "You can only borrow for a maximum of 15 days.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method if the borrow period exceeds 15 days
            }

            // Check if the student is registered
            boolean isRegistered = isStudentRegistered(studentID);

            // Set maximum books allowed based on registration status
            int maxBooksAllowed = isRegistered ? 5 : 1;

            // Check how many books the student currently has borrowed
            int currentBorrows = getCurrentBorrows(studentID);

            // Check if the student has exceeded their borrowing limit
            if (currentBorrows >= maxBooksAllowed) {
                JOptionPane.showMessageDialog(this, "You can only borrow up to " + maxBooksAllowed + " book(s)", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method if the borrowing limit is exceeded
            }

            CallableStatement callableStatement = connection.prepareCall("{CALL CheckoutCopy(?, ?, ?, ?)}");
            callableStatement.setInt(1, copyID);
            callableStatement.setInt(2, studentID);
            callableStatement.setDate(3, issueDate);
            callableStatement.setDate(4, dueDate);
            
            callableStatement.execute();
            
            // Update the NumberOfCopies column for the checked out copy
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE Copies SET NumberOfCopies = NumberOfCopies - 1 WHERE CopyID = ?");
            updateStatement.setInt(1, copyID);
            updateStatement.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Copy successfully checked out.");
            fetchCopies(); // Refresh the table after checking out the copy
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to checkout copy: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private int getCurrentBorrows(int studentID) {
        try {
            // Prepare SQL statement to count the number of borrows for the student
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS BorrowCount FROM Borrows WHERE StudentID = ? AND ActualReturnDate IS NULL");
            preparedStatement.setInt(1, studentID);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if there are any borrows for the student
            if (resultSet.next()) {
                return resultSet.getInt("BorrowCount");
            } else {
                // If no borrows found, return 0
                return 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // If an SQL exception occurs, return -1 to indicate an error
            return -1;
        }
    }

    private boolean isStudentRegistered(int studentID) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT IsRegisteredUniversity FROM Students WHERE StudentID = ?");
            preparedStatement.setInt(1, studentID);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getBoolean("IsRegisteredUniversity");
            } else {
                // If no student found with the given ID, return false
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // If an SQL exception occurs, return false
            return false;
        }
    }

    public static void main(String[] args) {
        // SwingUtilities.invokeLater(Copies::new);
        // Retrieve user type from login
        String userType = new Login().getUserTypeFromLogin();
        SwingUtilities.invokeLater(() -> new Copies(userType)); // Pass user type to Copies constructor
    }
}