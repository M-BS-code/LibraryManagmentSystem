import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Bookings extends JFrame {
    private Connection connection;
    private JTable table;
    private JTextField searchField;
    private JButton updateStatusButton;
    private JButton goBackButton;
    private JButton searchButton;
    private String userType; // Instance variable for storing user type

    public Bookings(String userType) {
        this.userType = userType; // Initialize userType with the provided value
        setTitle("Bookings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        goBackButton = new JButton("Go Back");
        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle navigation to the main menu here
                dispose(); // Close the current window

                // Open the main menu page
                String userType = new Login().getUserTypeFromLogin(); // Retrieve user type from login
                MainMenu mainMenu = new MainMenu(userType);
                mainMenu.setVisible(true);
            }
        });
        
    
        
        buttonPanel.add(goBackButton);

        searchField = new JTextField(20);
        buttonPanel.add(new JLabel("Search: "));
        buttonPanel.add(searchField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                searchBookings(query);
            } else {
                fetchBookings();
            }
        });
        buttonPanel.add(searchButton);

        add(buttonPanel, BorderLayout.NORTH);

        updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(e -> updateBookingStatus(userType));
        add(updateStatusButton, BorderLayout.SOUTH);

        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            fetchBookings();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        setVisible(true);
    }

    private void fetchBookings() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Bookings");

            // Create a DefaultTableModel to store bookings
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("BookingID");
            model.addColumn("RoomNumber");
            model.addColumn("StudentID");
            model.addColumn("StartDateTime");
            model.addColumn("EndDateTime");
            model.addColumn("Status"); // Add Status column

            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("BookingID"),
                        resultSet.getInt("RoomNumber"),
                        resultSet.getInt("StudentID"),
                        resultSet.getTimestamp("StartDateTime"), // Use getTimestamp for datetime fields
                        resultSet.getTimestamp("EndDateTime"),    // Use getTimestamp for datetime fields
                        resultSet.getString("Status") // Retrieve Status column
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchBookings(String query) {
        try {
            String sql = "SELECT * FROM Bookings WHERE BookingID LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + query + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a DefaultTableModel to store search results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("BookingID");
            model.addColumn("RoomNumber");
            model.addColumn("StudentID");
            model.addColumn("StartDateTime");
            model.addColumn("EndDateTime");

            // Populate the table model with search results
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("BookingID"),
                        resultSet.getInt("RoomNumber"),
                        resultSet.getInt("StudentID"),
                        resultSet.getTimestamp("StartDateTime"), // Use getTimestamp for datetime fields
                        resultSet.getTimestamp("EndDateTime")    // Use getTimestamp for datetime fields
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Update status method
    private void updateBookingStatus(String userType) {
        if (!userType.equals("Administrator")) {
            JOptionPane.showMessageDialog(this, "Only administrators can update booking status.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingID = (int) table.getValueAt(selectedRow, 0);
        String currentStatus = (String) table.getValueAt(selectedRow, 5); // Get the current status from the table

        try {
            CallableStatement callableStatement = connection.prepareCall("{CALL UpdateBookingRequest(?, ?, ?, ?, ?)}");
            callableStatement.setInt(1, bookingID);
            callableStatement.setTimestamp(2, (Timestamp) table.getValueAt(selectedRow, 3)); // StartDateTime
            callableStatement.setTimestamp(3, (Timestamp) table.getValueAt(selectedRow, 4)); // EndDateTime
            callableStatement.setInt(4, (int) table.getValueAt(selectedRow, 1)); // RoomNumber
            callableStatement.registerOutParameter(5, Types.VARCHAR); // Output parameter for status
            callableStatement.execute();

            String bookingStatus = callableStatement.getString(5); // Retrieve the status from the output parameter
            if (!bookingStatus.equals(currentStatus)) {
                // Update the status column in the table model
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setValueAt(bookingStatus, selectedRow, 5); // Update the "Status" column in the table model
                
                // Update availability of meeting room based on booking status
                updateMeetingRoomAvailability((int) table.getValueAt(selectedRow, 1), bookingStatus.equals("Approved"));

                JOptionPane.showMessageDialog(this, "Booking status updated to: " + bookingStatus, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update booking status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update booking status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMeetingRoomAvailability(int roomNumber, boolean availability) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE MeetingRooms SET Availability = ? WHERE roomNumber = ?");
            preparedStatement.setBoolean(1, availability);
            preparedStatement.setInt(2, roomNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update meeting room availability: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        String userType = new Login().getUserTypeFromLogin();
        SwingUtilities.invokeLater(() -> new Bookings(userType));
    }
}