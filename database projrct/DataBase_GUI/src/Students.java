import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Students extends JFrame {
    private Connection connection;
    private JTable table;
    private JTextField searchField;

    public Students() {
        setTitle("Students Table");
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
                String userType = new Login().getUserTypeFromLogin(); // Retrieve user type from login
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
                searchStudents(query);
            } else {
                fetchStudents(); // If search query is empty, fetch all students
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

        // Connect to the database and fetch students
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            fetchStudents();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Make the frame visible after all components are added
        setVisible(true);
    }

    private void fetchStudents() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Students");

            // Create a DefaultTableModel to store students
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("StudentID");
            model.addColumn("UserID");
            model.addColumn("FirstName");
            model.addColumn("LastName");
            model.addColumn("Email");
            model.addColumn("IsRegisteredUniversity");


            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("StudentID"),
                        resultSet.getInt("UserID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getString("Email"),
                        resultSet.getString("IsRegisteredUniversity")

                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchStudents(String query) {
        try {
            String sql = "SELECT * FROM Students WHERE StudentID LIKE ? OR UserID LIKE ? OR FirstName LIKE ? OR LastName LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= 4; i++) {
                preparedStatement.setString(i, "%" + query + "%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a DefaultTableModel to store search results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("StudentID");
            model.addColumn("UserID");
            model.addColumn("FirstName");
            model.addColumn("LastName");
            model.addColumn("Email");
            model.addColumn("IsRegisteredUniversity");

            // Populate the table model with search results
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("StudentID"),
                        resultSet.getInt("UserID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getString("Email"),
                        resultSet.getString("IsRegisteredUniversity")
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Students::new);
    }
}
