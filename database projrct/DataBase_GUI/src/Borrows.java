import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Borrows extends JFrame {

    // Database connection
    private Connection connection;

    // GUI components
    private JTable table;
    private JTextField searchField;
    private String userType;

    public Borrows(String userType) {
        this.userType = userType;
        setTitle("Borrows Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create a JPanel for search bar
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);

        // Button to navigate back to the main menu
        JButton menuButton = new JButton("Menu");
        menuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle navigation to the main menu here
                dispose(); // Close the current window

                // Open the main menu page
                String userType = new Login().getUserTypeFromLogin(); // Retrieve user type from login
                MainMenu mainMenu = new MainMenu(userType);
                mainMenu.setVisible(true);
            }
        });
        searchPanel.add(menuButton);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().trim();
                if (!query.isEmpty()) {
                    searchBorrows(query);
                } else {
                    fetchData(); // If search query is empty, fetch all data
                }
            }
        });
        searchPanel.add(searchButton);

        getContentPane().add(searchPanel, BorderLayout.NORTH);

        // Create a new JTable
        table = new JTable();
        customizeTableAppearance(table); // Customize table appearance
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Create a JPanel for buttons in the SOUTH region
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center-align the buttons

        // Button for listing books borrowed by a student
        JButton booksBorrowedByStudentButton = new JButton("Borrowed Books");
        booksBorrowedByStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userType.equals("Librarian") || userType.equals("Administrator")) {
                    int studentID = Integer.parseInt(JOptionPane.showInputDialog("Enter Student ID:"));
                    BooksBorrowedByStudent(studentID);
                } else {
                    JOptionPane.showMessageDialog(null, "Access Denied. Only Librarians and Administrators can access this feature.");
                }
            }
        });
        buttonPanel.add(booksBorrowedByStudentButton);

        // Button for returning a resource
        JButton returnButton = new JButton("Return Resource");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userType.equals("Librarian") || userType.equals("Administrator")) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) { // If a row is selected
                        int borrowID = (int) table.getValueAt(selectedRow, 0);
                        returnResource(borrowID);
                    } else { // If no row is selected
                        JOptionPane.showMessageDialog(null, "Please select a borrowed book to return.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Access Denied. Only Librarians and Administrators can access this feature.");
                }
            }
        });
        buttonPanel.add(returnButton);

        // Button for listing overdue books
        JButton overdueButton = new JButton("Overdue Books");
        overdueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userType.equals("Librarian") || userType.equals("Administrator")) {
                    listOverdueBooks();
                } else {
                    JOptionPane.showMessageDialog(null, "Access Denied. Only Librarians and Administrators can access this feature.");
                }
            }
        });
        buttonPanel.add(overdueButton);

        // Add the button panel to the frame
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Connect to the database and fetch data
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            fetchData();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    // Fetch all borrow records from the database
    private void fetchData() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Borrows");
            populateTableModel(resultSet);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    // Search for borrow records based on query
    private void searchBorrows(String query) {
        try {
            String sql = "SELECT * FROM Borrows WHERE BorrowID LIKE ? OR StudentID LIKE ? OR CopyID LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + query + "%");
            preparedStatement.setString(2, "%" + query + "%");
            preparedStatement.setString(3, "%" + query + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            populateTableModel(resultSet);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    // Customize the appearance of the JTable
    private void customizeTableAppearance(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    // Populate table model with data from ResultSet
    private void populateTableModel(ResultSet resultSet) throws SQLException {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("BorrowID");
        model.addColumn("StudentID");
        model.addColumn("CopyID");
        model.addColumn("IssueDate");
        model.addColumn("DueDate");
        model.addColumn("ActualReturnDate"); // Add the new column for ActualReturnDate

        while (resultSet.next()) {
            Object[] row = {
                resultSet.getInt("BorrowID"), // Ensure correct column name here
                resultSet.getInt("StudentID"),
                resultSet.getInt("CopyID"),
                resultSet.getDate("IssueDate"),
                resultSet.getDate("DueDate"),
                resultSet.getDate("ActualReturnDate") // Get ActualReturnDate from ResultSet
            };
            model.addRow(row);
        }

        table.setModel(model);
    }

    // Handle returning a resource
    private void returnResource(int borrowID) {
        try {
            CallableStatement callableStatement = connection.prepareCall("{CALL ReturnResource(?)}");
            callableStatement.setInt(1, borrowID);
            callableStatement.execute();
            JOptionPane.showMessageDialog(this, "Resource returned successfully!");
            fetchData(); // Refresh the table after returning the resource
        } catch (SQLException ex) {
            handleSQLException(ex);
        }
    }

    // List overdue books
    private void listOverdueBooks() {
        try {
            CallableStatement callableStatement = connection.prepareCall("{CALL ListOverdueBooks()}");
            ResultSet resultSet = callableStatement.executeQuery();

            // Create a DefaultTableModel for overdue books
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("BorrowID");
            model.addColumn("StudentID");
            model.addColumn("StudentName");
            model.addColumn("Email");
            model.addColumn("CopyID");
            model.addColumn("Title");
            model.addColumn("IssueDate");
            model.addColumn("DueDate");

            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("BorrowID"),
                    resultSet.getInt("StudentID"),
                    resultSet.getString("FirstName") + " " + resultSet.getString("LastName"), // Concatenate first and last names
                    resultSet.getString("Email"),
                    resultSet.getInt("CopyID"),
                    resultSet.getString("Title"),
                    resultSet.getDate("IssueDate"),
                    resultSet.getDate("DueDate")
                };
                model.addRow(row);
            }

            table.setModel(model); // Set the table model to the JTable
        } catch (SQLException ex) {
            handleSQLException(ex);
        }
    }

    // List books borrowed by a student
    private void BooksBorrowedByStudent(int studentID) {
        try {
            CallableStatement callableStatement = connection.prepareCall("{CALL BooksBorrowedByStudent(?)}");
            callableStatement.setInt(1, studentID);
            ResultSet resultSet = callableStatement.executeQuery();

            // Create a DefaultTableModel without the ActualReturnDate column
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("BorrowID");
            model.addColumn("StudentID");
            model.addColumn("CopyID");
            model.addColumn("ResourceID");
            model.addColumn("Barcode");
            model.addColumn("Price");
            model.addColumn("PurchaseDate");
            model.addColumn("RackNumber");
            model.addColumn("NumberOfCopies");
            model.addColumn("IssueDate");
            model.addColumn("DueDate");

            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("BorrowID"),
                    resultSet.getInt("StudentID"),
                    resultSet.getInt("CopyID"),
                    resultSet.getInt("ResourceID"),
                    resultSet.getString("Barcode"),
                    resultSet.getDouble("Price"),
                    resultSet.getDate("PurchaseDate"),
                    resultSet.getInt("RackNumber"),
                    resultSet.getInt("NumberOfCopies"),
                    resultSet.getDate("IssueDate"),
                    resultSet.getDate("DueDate")
                };
                model.addRow(row);
            }

            table.setModel(model); // Set the table model to the JTable
        } catch (SQLException ex) {
            handleSQLException(ex);
        }
    }


    // Handle SQL exceptions
    private void handleSQLException(SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Retrieve user type from login
            String userType = new Login().getUserTypeFromLogin();
            Borrows borrowsGUI = new Borrows(userType);
            borrowsGUI.setVisible(true);
        });
    }
}
