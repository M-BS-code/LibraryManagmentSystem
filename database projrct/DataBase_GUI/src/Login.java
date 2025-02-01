import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login {

    private JFrame frame;
    private JTextField textFieldUsername;
    private JPasswordField passwordField;
    private String userType; // Declare userType variable


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login window = new Login();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Login() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblUsername.setBounds(60, 50, 80, 20);
        frame.getContentPane().add(lblUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPassword.setBounds(60, 100, 80, 20);
        frame.getContentPane().add(lblPassword);

        textFieldUsername = new JTextField();
        textFieldUsername.setBounds(150, 50, 200, 25);
        frame.getContentPane().add(textFieldUsername);
        textFieldUsername.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 25);
        frame.getContentPane().add(passwordField);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textFieldUsername.getText();
                String password = String.valueOf(passwordField.getPassword());

                // Perform authentication logic here
                String userType = authenticateUserAndGetUserType(username, password);
                if (userType != null) {
                    // Redirect user based on their role
                    redirectUser(userType);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.");
                }
            }
        });
        btnLogin.setBounds(150, 150, 100, 30);
        frame.getContentPane().add(btnLogin);
    }

    /**
     * Authenticate the user and retrieve the user type from the database.
     */
    private String authenticateUserAndGetUserType(String username, String password) {
        String userType = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            String query = "SELECT UserType FROM Users WHERE UserName = ? AND Password = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            resultSet = statement.executeQuery();
            
            
            if (resultSet.next()) {
                userType = resultSet.getString("UserType");
                // Set the userType instance variable
                this.userType = userType;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
   
    
        
        
        
        

        return userType;
    }

    
    
    public String getUserTypeFromLogin() {

        return userType;
    }
    
    
 

    /**
     * Redirect the user to the appropriate main menu based on their role.
     */
    private void redirectUser(String userType) {
        if (userType.equals("Administrator")) {
             JOptionPane.showMessageDialog(frame, "Welcome, Administrator!");
             MainMenu administratorMenu = new MainMenu(userType);
             administratorMenu.setVisible(true);
             
             
         } else if (userType.equals("Student")) {
             JOptionPane.showMessageDialog(frame, "Welcome, Student!");
             MainMenu studentMenu = new MainMenu(userType);
            studentMenu.setVisible(true);
            
            
        } else if (userType.equals("Librarian")) {
            JOptionPane.showMessageDialog(frame, "Welcome, Librarian!");
            MainMenu librarianMenu = new MainMenu(userType);
            librarianMenu.setVisible(true);
        }
        frame.dispose(); // Close login window after successful login
    }

}
