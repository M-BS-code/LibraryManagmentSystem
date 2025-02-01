import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LibraryCards extends JFrame {
    private Connection connection;
    private JTable table;
    private JTextField searchField;
    private String userType; // Add new field to store user type

    public LibraryCards(String userType) { // Modify constructor to accept user type
        this.userType = userType; // Store user type

        setTitle("Library Cards");
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
                navigateToMainMenu();
            }
        });


        searchPanel.add(btnNewButton);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                searchLibraryCards(query);
            } else {
                fetchLibraryCards(); // If search query is empty, fetch all library cards
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

        // Connect to the database
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Add apply button for library card
        if (userType.equals("Administrator") || userType.equals("Librarian")) { // Check user type
            JButton applyButton = new JButton("Apply for Library Card");
            applyButton.addActionListener(e -> applyForLibraryCard());
            getContentPane().add(applyButton, BorderLayout.SOUTH);
        }

        // Fetch library cards from the database
        fetchLibraryCards();

        // Make the frame visible after all components are added
        setVisible(true);
    }


    private void fetchLibraryCards() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM LibraryCards");

            // Create a DefaultTableModel to store library cards
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("CardID");
            model.addColumn("UserID");
            model.addColumn("StudentID");
            model.addColumn("CardActivationDate");
            model.addColumn("Status");

            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("CardID"),
                        resultSet.getInt("UserID"),
                        resultSet.getInt("StudentID"),
                        resultSet.getDate("CardActivationDate"),
                        resultSet.getString("Status")
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchLibraryCards(String query) {
        try {
            String sql = "SELECT * FROM LibraryCards WHERE CardID LIKE ?OR UserID LIKE ? OR StudentID LIKE ? OR CardActivationDate LIKE ? OR Status LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= 4; i++) {
                preparedStatement.setString(i, "%" + query + "%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a DefaultTableModel to store search results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("CardID");     
            model.addColumn("UserID");
            model.addColumn("StudentID");
            model.addColumn("CardActivationDate");
            model.addColumn("Status");

            // Populate the table model with search results
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("CardID"),
                        resultSet.getInt("UserID"),
                        resultSet.getInt("StudentID"),
                        resultSet.getDate("CardActivationDate"),
                        resultSet.getString("Status")
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int getStudentID(String firstName, String lastName) {
        int studentID = -1; // Default value indicating student not found

        try {
            // Prepare the SQL statement to retrieve StudentID based on first name and last name
            String sql = "SELECT StudentID FROM Students WHERE FirstName = ? AND LastName = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            // Execute the SQL statement
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if the result set contains any rows
            if (resultSet.next()) {
                // Retrieve the StudentID from the result set
                studentID = resultSet.getInt("StudentID");
            } else {
                JOptionPane.showMessageDialog(this, "Student does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving student ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return studentID;
    }

    private void applyForLibraryCard() {
        // Gather input from the user
        String firstName = JOptionPane.showInputDialog(this, "Enter First Name:");
        String lastName = JOptionPane.showInputDialog(this, "Enter Last Name:");

        try {
            // Retrieve StudentID based on user's information
            int studentID = getStudentID(firstName, lastName);

            // Check if the student exists
            if (studentID == -1) {
                JOptionPane.showMessageDialog(this, "Student does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if the student already has a library card
            String checkSql = "SELECT * FROM LibraryCards WHERE StudentID = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, studentID);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Student already has a library card, so we update its status to 'Active'
                String updateSql = "UPDATE LibraryCards SET Status = 'Active', CardActivationDate = CURDATE() WHERE StudentID = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, studentID);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Library card status updated to active.");
            } else {
                // Student does not have a library card, so we insert a new record
                String insertSql = "INSERT INTO LibraryCards (StudentID, CardActivationDate, Status) VALUES (?, CURDATE(), 'Active')";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setInt(1, studentID);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Library card application successful!");
            }
            fetchLibraryCards(); // Refresh the table after updating or applying for a library card
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update library card status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    
    
    private void navigateToMainMenu() {
        dispose(); // Close the current window
        MainMenu mainMenu = new MainMenu(userType); // Pass user type to the main menu
        mainMenu.setVisible(true);
    }
    public static void main(String[] args) {
        // Retrieve user type from login process
        String userType = new Login().getUserTypeFromLogin(); // Example login method

        // Create an instance of LibraryCards with the user type
        SwingUtilities.invokeLater(() -> new LibraryCards(userType));
    }
}
