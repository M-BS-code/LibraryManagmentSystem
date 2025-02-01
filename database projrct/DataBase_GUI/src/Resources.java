import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Resources extends JFrame {
    private Connection connection;
    private JTable table;
    private JTextField searchField;
    private String userType; // Add new field to store user type


    public Resources(String userType) {
        this.userType = userType;
        setTitle("Resources Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create a JPanel for search bar at the top
        JPanel searchPanelTop = new JPanel();
        searchField = new JTextField(20);

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

        searchPanelTop.add(menuButton);
        searchPanelTop.add(new JLabel("Search: "));
        searchPanelTop.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                searchResources(query);
            } else {
                fetchResources();
            }
        });
        searchPanelTop.add(searchButton);

        JButton availableResourcesButton = new JButton("Available Resources");
        availableResourcesButton.addActionListener(e -> BrowseAvailableResources());
        searchPanelTop.add(availableResourcesButton);

        getContentPane().add(searchPanelTop, BorderLayout.NORTH);

        JPanel searchPanelBottom = new JPanel();

     // Check user type and add buttons accordingly
     if (!"Student".equals(userType)) {
         JButton deleteResourceWithAssociationsButton = new JButton("Delete Resource with Associations");
         deleteResourceWithAssociationsButton.addActionListener(e -> {
             int selectedRow = table.getSelectedRow();
             if (selectedRow != -1) {
                 int resourceID = (int) table.getValueAt(selectedRow, 0);
                 deleteResourceWithAssociations(resourceID);
             } else {
                 JOptionPane.showMessageDialog(Resources.this, "Please select a resource to delete.", "Error", JOptionPane.ERROR_MESSAGE);
             }
         });
         searchPanelBottom.add(deleteResourceWithAssociationsButton);

         JButton updateResourceButton = new JButton("Update Resource");
         updateResourceButton.addActionListener(e -> {
             int selectedRow = table.getSelectedRow();
             if (selectedRow != -1) {
                 int resourceID = (int) table.getValueAt(selectedRow, 0);
                 updateResource(resourceID);
             } else {
                 JOptionPane.showMessageDialog(Resources.this, "Please select a resource to update.", "Error", JOptionPane.ERROR_MESSAGE);
             }
         });
         searchPanelBottom.add(updateResourceButton);

         JButton addResourceButton = new JButton("Add Resource");
         addResourceButton.addActionListener(e -> addResource());
         searchPanelBottom.add(addResourceButton);
     }

     // Add the search panel bottom to the content pane
     getContentPane().add(searchPanelBottom, BorderLayout.SOUTH);


        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            fetchResources();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        setVisible(true);
    }

  

    private void refreshData() {
        fetchResources(); // Call the method to fetch resources from the database and update the table
    }

    private void fetchResources() {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Resources")) {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ResourceID");
            model.addColumn("ResourceType");
            model.addColumn("Title");
            model.addColumn("EditionNumber");
            model.addColumn("PublicationDate");
            model.addColumn("Publisher");
            model.addColumn("Editor");
            model.addColumn("Subject");
            model.addColumn("Language");
            model.addColumn("Author");
            model.addColumn("ProductionYear");

            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("ResourceID"),
                        resultSet.getString("ResourceType"),
                        resultSet.getString("Title"),
                        resultSet.getObject("EditionNumber"),
                        resultSet.getObject("PublicationDate"),
                        resultSet.getString("Publisher"),
                        resultSet.getString("Editor"),
                        resultSet.getString("Subject"),
                        resultSet.getString("Language"),
                        resultSet.getString("Author"),
                        resultSet.getInt("ProductionYear")
                };
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch resources: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void searchResources(String query) {
        String sql = "SELECT * FROM Resources WHERE ResourceID LIKE ? OR ResourceType LIKE ? OR Title LIKE ? OR EditionNumber LIKE ? OR PublicationDate LIKE ? OR Publisher LIKE ? OR Editor LIKE ? OR ProductionYear LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 8; i++) {
                preparedStatement.setString(i, "%" + query + "%");
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("ResourceID");
                model.addColumn("ResourceType");
                model.addColumn("Title");
                model.addColumn("EditionNumber");
                model.addColumn("PublicationDate");
                model.addColumn("Publisher");
                model.addColumn("Editor");
                model.addColumn("Subject");
                model.addColumn("Language");
                model.addColumn("Author");
                model.addColumn("ProductionYear");

                while (resultSet.next()) {
                    Object[] row = {
                            resultSet.getInt("ResourceID"),
                            resultSet.getString("ResourceType"),
                            resultSet.getString("Title"),
                            resultSet.getObject("EditionNumber"),
                            resultSet.getObject("PublicationDate"),
                            resultSet.getString("Publisher"),
                            resultSet.getString("Editor"),
                            resultSet.getString("Subject"),
                            resultSet.getString("Language"),
                            resultSet.getString("Author"),
                            resultSet.getInt("ProductionYear")
                    };
                    model.addRow(row);
                }
                table.setModel(model);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to search resources: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }}
    private void deleteResource(int resourceID) {
        try (CallableStatement deleteResourceStatement = connection.prepareCall("{CALL DeleteResource(?)}")) {
            deleteResourceStatement.setInt(1, resourceID);
            deleteResourceStatement.executeUpdate(); // Use executeUpdate() for DELETE operations

            // Now, after deleting the resource, remove the row from the table model
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                if ((int) model.getValueAt(i, 0) == resourceID) {
                    model.removeRow(i);
                    break;
                }
            }
            JOptionPane.showMessageDialog(this, "Resource deleted successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete resource: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void deleteResourceWithAssociations(int resourceID) {
        try {
            // Delete the resource directly from the Resources table
            CallableStatement deleteResourceStatement = connection.prepareCall("{CALL DeleteResource(?)}");
            deleteResourceStatement.setInt(1, resourceID);
            deleteResourceStatement.executeUpdate(); // Use executeUpdate() for DELETE operations

            JOptionPane.showMessageDialog(this, "Resource deleted successfully!");
            fetchResources(); // Refresh the table after deleting the resource
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete resource: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addResource() {
        // Prompt user for resource information
        String resourceType = JOptionPane.showInputDialog(Resources.this, "Enter Resource Type:");
        String title = JOptionPane.showInputDialog(Resources.this, "Enter Title:");
        String editionNumberStr = JOptionPane.showInputDialog(Resources.this, "Enter Edition Number:");
        String publicationDateStr = JOptionPane.showInputDialog(Resources.this, "Enter Publication Date (yyyy-MM-dd):");
        String publisher = JOptionPane.showInputDialog(Resources.this, "Enter Publisher:");
        String editor = JOptionPane.showInputDialog(Resources.this, "Enter Editor:");
        String subject = JOptionPane.showInputDialog(Resources.this, "Enter Subject:");
        String language = JOptionPane.showInputDialog(Resources.this, "Enter Language:");
        String author = JOptionPane.showInputDialog(Resources.this, "Enter Author:");
        String productionYearStr = JOptionPane.showInputDialog(Resources.this, "Enter Production Year:");
        String numberOfCopiesStr = JOptionPane.showInputDialog(Resources.this, "Enter Number of Copies:");
        String copyIDStr = JOptionPane.showInputDialog(Resources.this, "Enter Copy ID:");

        int editionNumber;
        int productionYear;
        int numberOfCopies;
        int copyID;
        Date publicationDate;
        try {
            // Parse input values
            editionNumber = Integer.parseInt(editionNumberStr);
            publicationDate = Date.valueOf(publicationDateStr);
            productionYear = Integer.parseInt(productionYearStr);
            numberOfCopies = Integer.parseInt(numberOfCopiesStr);
            copyID = Integer.parseInt(copyIDStr); // Parse the copy ID
        } catch (IllegalArgumentException ex) {
            // Handle invalid input
            JOptionPane.showMessageDialog(Resources.this, "Invalid input for edition number, publication date, production year, number of copies, or copy ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (CallableStatement callableStatement = connection.prepareCall("{CALL AddResource(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            // Set stored procedure parameters for the resource
            callableStatement.setString(1, resourceType);
            callableStatement.setString(2, title);
            callableStatement.setInt(3, editionNumber);
            callableStatement.setDate(4, publicationDate);
            callableStatement.setString(5, publisher);
            callableStatement.setString(6, editor);
            callableStatement.setString(7, subject);
            callableStatement.setString(8, language);
            callableStatement.setString(9, author);
            callableStatement.setInt(10, productionYear);
            callableStatement.setInt(11, numberOfCopies); // Pass the number of copies to the stored procedure
            callableStatement.setInt(12, copyID); // Pass the copy ID to the stored procedure

            // Execute the stored procedure to add the resource and its copies
            callableStatement.execute();
            JOptionPane.showMessageDialog(this, "Resource and its copies added successfully!");
            fetchResources(); // Refresh the table after adding the resource
        } catch (SQLException ex) {
            // Handle SQL exception
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add resource: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void updateResource(int resourceID) {
        String resourceType = JOptionPane.showInputDialog(Resources.this, "Enter new Resource Type:");
        String title = JOptionPane.showInputDialog(Resources.this, "Enter new Title:");
        String editionNumberStr = JOptionPane.showInputDialog(Resources.this, "Enter new Edition Number:");
        String publicationDateStr = JOptionPane.showInputDialog(Resources.this, "Enter new Publication Date (yyyy-MM-dd):");
        String publisher = JOptionPane.showInputDialog(Resources.this, "Enter new Publisher:");
        String editor = JOptionPane.showInputDialog(Resources.this, "Enter new Editor:");
        String subject = JOptionPane.showInputDialog(Resources.this, "Enter new Subject:");
        String language = JOptionPane.showInputDialog(Resources.this, "Enter new Language:");
        String author = JOptionPane.showInputDialog(Resources.this, "Enter new Author:");
        String productionYearStr = JOptionPane.showInputDialog(Resources.this, "Enter new Production Year:");

        int editionNumber;
        int productionYear;
        Date publicationDate;
        try {
            editionNumber = Integer.parseInt(editionNumberStr);
            publicationDate = Date.valueOf(publicationDateStr);
            productionYear = Integer.parseInt(productionYearStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(Resources.this, "Invalid input for edition number, publication date, or production year", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (CallableStatement callableStatement = connection.prepareCall("{CALL UpdateResource(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            callableStatement.setInt(1, resourceID);
            callableStatement.setString(2, resourceType);
            callableStatement.setString(3, title);
            callableStatement.setInt(4, editionNumber);
            callableStatement.setDate(5, publicationDate);
            callableStatement.setString(6, publisher);
            callableStatement.setString(7, editor);
            callableStatement.setString(8, subject);
            callableStatement.setString(9, language);
            callableStatement.setString(10, author);
            callableStatement.setInt(11, productionYear);

            callableStatement.execute();
            JOptionPane.showMessageDialog(this, "Resource updated successfully!");
            fetchResources(); // Refresh the table after updating the resource
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update resource: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void BrowseAvailableResources() {
        try {
            String sql = "CALL BrowseAvailableResources()";

            try (CallableStatement statement = connection.prepareCall(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("ResourceID");
                model.addColumn("ResourceType");
                model.addColumn("Title");
                model.addColumn("EditionNumber");
                model.addColumn("PublicationDate");
                model.addColumn("Publisher");
                model.addColumn("Editor");
                model.addColumn("Subject");
                model.addColumn("Language");
                model.addColumn("Author");
                model.addColumn("ProductionYear");
                model.addColumn("Availability");

                while (resultSet.next()) {
                    Object[] row = {
                        resultSet.getInt("ResourceID"),
                        resultSet.getString("ResourceType"),
                        resultSet.getString("Title"),
                        resultSet.getObject("EditionNumber"),
                        resultSet.getObject("PublicationDate"),
                        resultSet.getString("Publisher"),
                        resultSet.getString("Editor"),
                        resultSet.getString("Subject"),
                        resultSet.getString("Language"),
                        resultSet.getString("Author"),
                        resultSet.getInt("ProductionYear"),
                        resultSet.getString("Availability")
                    };
                    model.addRow(row);
                }
                table.setModel(model);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to browse available resources: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String userType = new Login().getUserTypeFromLogin(); // Retrieve user type from login
            Resources meetingRooms = new Resources(userType);
        });
    }
}