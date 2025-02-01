import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame {
    private String userType;

    public MainMenu(String userType) {
        this.userType = userType;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Main Menu - " + userType);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10)); // 3 rows, 3 columns
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton bookingsButton = new JButton("Bookings");
        JButton borrowsButton = new JButton("Borrows");
        JButton copiesButton = new JButton("Copies");
        JButton libraryCardsButton = new JButton("Library Cards");
        JButton meetingRoomsButton = new JButton("Meeting Rooms");
         JButton resourcesButton = new JButton("Resources");
        JButton studentsButton = new JButton("Students");
        JButton usersButton = new JButton("Users");

        bookingsButton.addActionListener(e -> openFrame(new Bookings(userType)));
        borrowsButton.addActionListener(e -> openFrame(new Borrows(userType)));
        copiesButton.addActionListener(e -> openFrame(new Copies(userType)));
        libraryCardsButton.addActionListener(e -> openFrame(new LibraryCards(userType)));
        meetingRoomsButton.addActionListener(e -> openFrame(new MeetingRooms(userType)));
        resourcesButton.addActionListener(e -> openFrame(new Resources(userType)));
        studentsButton.addActionListener(e -> openFrame(new Students()));
        usersButton.addActionListener(e -> openFrame(new Users(userType)));

        buttonPanel.add(bookingsButton);
        buttonPanel.add(borrowsButton);
        buttonPanel.add(copiesButton);
        buttonPanel.add(libraryCardsButton);
        buttonPanel.add(meetingRoomsButton);
        buttonPanel.add(resourcesButton);
        buttonPanel.add(studentsButton);
        buttonPanel.add(usersButton);

        // Disable the Students button for students
        if (userType.equals("Student")) {
            studentsButton.setEnabled(false);
            libraryCardsButton.setEnabled(false);
            usersButton.setEnabled(false);


        }

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openFrame(JFrame frame) {
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshMenu();
            }
        });
    }

    private void refreshMenu() {
        getContentPane().removeAll();
        initializeUI();
    }
 
    public static void main(String[] args) {
        // Assuming you have a method getUserTypeFromLogin() in your Login class to retrieve the user type
        Login login = new Login();
        String userType = login.getUserTypeFromLogin();
        SwingUtilities.invokeLater(() -> new MainMenu(userType));
    }
}

