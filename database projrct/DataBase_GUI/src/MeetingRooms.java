import javax.swing.*;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class MeetingRooms extends JFrame {
    private Connection connection;
    private JTable table;
    private JTextField searchField;
    private String userType; // Add new field to store user type


    public MeetingRooms(String userType) {
        this.userType = userType;
        setTitle("Meeting Rooms");
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
                searchMeetingRooms(query);
            } else {
                fetchMeetingRooms(); // If search query is empty, fetch all meeting rooms
            }
        });
        
    
        
        searchPanel.add(searchButton);
        getContentPane().add(searchPanel, BorderLayout.NORTH);

        JButton addRoomButton = new JButton("Add Meeting Room");
        addRoomButton.addActionListener(e -> {
            if (userType.equals("Administrator") || userType.equals("Librarian")) {
                addMeetingRoom();
            } else {
                JOptionPane.showMessageDialog(this, "You don't have the privilege to add meeting rooms.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton updateRoomButton = new JButton("Update Meeting Room");
        updateRoomButton.addActionListener(e -> {
            if (userType.equals("Administrator") || userType.equals("Librarian")) {
                updateMeetingRoom();
            } else {
                JOptionPane.showMessageDialog(this, "You don't have the privilege to update meeting rooms.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        
        
        
        JButton reserveRoomButton = new JButton("Reserve Room");
        reserveRoomButton.addActionListener(e -> {
            if (userType.equals("Student")) {
                reserveRoom(); // Call the method to handle room reservation
            } else {
                JOptionPane.showMessageDialog(this, "You don't have the privilege to reserve meeting rooms.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        });

      
        
        
        
        JButton searchAvailableRoomsButton = new JButton("Search Available Rooms");
        searchAvailableRoomsButton.addActionListener(e -> searchAvailableRooms());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addRoomButton);
        buttonPanel.add(updateRoomButton);
        buttonPanel.add(searchAvailableRoomsButton);
        buttonPanel.add(reserveRoomButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

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

        // Connect to the database and fetch meeting rooms
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            fetchMeetingRooms();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Make the frame visible after all components are added
        setVisible(true);
    }

    private void fetchMeetingRooms() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM MeetingRooms");

            // Create a DefaultTableModel to store meeting rooms
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Room Number");
            model.addColumn("Capacity");
            model.addColumn("Equipment");
            model.addColumn("Maintenance Schedule");
            model.addColumn("Availability");

            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("roomNumber"),
                        resultSet.getInt("Capacity"),
                        resultSet.getString("Equipment"),
                        resultSet.getString("MaintenanceSchedule"),
                        resultSet.getBoolean("Availability")
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchMeetingRooms(String query) {
        try {
            String sql = "SELECT * FROM MeetingRooms WHERE roomNumber LIKE ? OR Equipment LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + query + "%");
            preparedStatement.setString(2, "%" + query + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a DefaultTableModel to store search results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Room Number");
            model.addColumn("Capacity");
            model.addColumn("Equipment");
            model.addColumn("Maintenance Schedule");
            model.addColumn("Availability");

            // Populate the table model with search results
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("roomNumber"),
                        resultSet.getInt("Capacity"),
                        resultSet.getString("Equipment"),
                        resultSet.getString("MaintenanceSchedule"),
                        resultSet.getBoolean("Availability")
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addMeetingRoom() {
        try {
            // Get the maximum room number from the database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(roomNumber) FROM MeetingRooms");
            int nextRoomNumber = 1; // Default to 1 if no meeting rooms exist yet
            if (resultSet.next()) {
                nextRoomNumber = resultSet.getInt(1) + 1;
            }

            // Gather input for new meeting room
            int capacity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Capacity:"));
            String equipment = JOptionPane.showInputDialog(this, "Enter Equipment:");
            String maintenanceSchedule = JOptionPane.showInputDialog(this, "Enter Maintenance Schedule:");
            boolean availability = Boolean.parseBoolean(JOptionPane.showInputDialog(this, "Enter Availability (true/false):"));

            // Call stored procedure to add the meeting room
            CallableStatement callableStatement = connection.prepareCall("{CALL AddMeetingRoom(?, ?, ?, ?, ?)}");
            callableStatement.setInt(1, nextRoomNumber); // Pass the next available room number
            callableStatement.setInt(2, capacity);
            callableStatement.setString(3, equipment);
            callableStatement.setString(4, maintenanceSchedule);
            callableStatement.setBoolean(5, availability);
            callableStatement.execute();
            JOptionPane.showMessageDialog(this, "Meeting room added successfully!");
            fetchMeetingRooms(); // Refresh the table after adding the meeting room
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add meeting room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateMeetingRoom() {
        // Prompt for the Room Number to update
        int roomNumber = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Room Number to update:"));

        // Gather input for updating meeting room
        int capacity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Capacity:"));
        String equipment = JOptionPane.showInputDialog(this, "Enter Equipment:");
        String maintenanceSchedule = JOptionPane.showInputDialog(this, "Enter Maintenance Schedule:");
        boolean availability = Boolean.parseBoolean(JOptionPane.showInputDialog(this, "Enter Availability (true/false):"));

        // Call stored procedure to update the meeting room
        try {
            CallableStatement callableStatement = connection.prepareCall("{CALL UpdateMeetingRoom(?, ?, ?, ?, ?)}");
            callableStatement.setInt(1, roomNumber);
            callableStatement.setInt(2, capacity);
            callableStatement.setString(3, equipment);
            callableStatement.setString(4, maintenanceSchedule);
            callableStatement.setBoolean(5, availability);
            callableStatement.execute();
            JOptionPane.showMessageDialog(this, "Meeting Room updated successfully!");
            fetchMeetingRooms(); // Refresh the table after updating the meeting room
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update meeting room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAvailableRooms() {
        // Trigger search for available meeting rooms
        searchAvailableMeetingRooms();
    }

    private void searchAvailableMeetingRooms() {
        try {
            // Prepare the SQL statement to call the stored procedure
            String sql = "CALL SearchAvailableMeetingRooms()";
            CallableStatement callableStatement = connection.prepareCall(sql);

            // Execute the stored procedure
            ResultSet resultSet = callableStatement.executeQuery();

            // Create a DefaultTableModel to store search results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Room Number");
            model.addColumn("Capacity");
            model.addColumn("Equipment");

            // Populate the table model with search results
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("roomNumber"),
                        resultSet.getInt("Capacity"),
                        resultSet.getString("Equipment")
                };
                model.addRow(row);
            }

            // Set the table model to the JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to search available meeting rooms: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void reserveRoom() {
        try {
            int BookingID = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter BookingID:"));
            int roomNumber = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Room Number:"));
            String studentID = JOptionPane.showInputDialog(this, "Enter Student ID:");

            // Prompt for start date and time
            String startDateTimeInput = JOptionPane.showInputDialog(this, "Enter Start Date and Time (yyyy-MM-dd HH:mm):");
            LocalDateTime startDateTime = parseDateTime(startDateTimeInput);

            // Prompt for end date and time
            String endDateTimeInput = JOptionPane.showInputDialog(this, "Enter End Date and Time (yyyy-MM-dd HH:mm):");
            LocalDateTime endDateTime = parseDateTime(endDateTimeInput);

            if (startDateTime != null && endDateTime != null) {
                // Call the stored procedure to reserve the room
                CallableStatement callableStatement = connection.prepareCall("{CALL ReserveRoom(?, ?, ?, ?, ?)}");
                callableStatement.setInt(1, roomNumber);
                callableStatement.setInt(2, Integer.parseInt(studentID));
                callableStatement.setTimestamp(3, Timestamp.valueOf(startDateTime));
                callableStatement.setTimestamp(4, Timestamp.valueOf(endDateTime));
                callableStatement.registerOutParameter(5, Types.VARCHAR); // OUT parameter for status

                callableStatement.execute(); // Execute the stored procedure

                String status = callableStatement.getString(5); // Retrieve status using parameter index 5
                JOptionPane.showMessageDialog(this, "Reservation status: " + status);

                fetchMeetingRooms(); // Refresh the table after reservation
            } else {
                JOptionPane.showMessageDialog(this, "Invalid date or time format. Please enter date and time in the format: yyyy-MM-dd HH:mm",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to reserve room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException ex) {
            return null; // Return null if parsing fails
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String userType = new Login().getUserTypeFromLogin(); // Retrieve user type from login
            MeetingRooms meetingRooms = new MeetingRooms(userType);
        });
    }
}