import java.sql.Connection;
import java.sql.DriverManager;

public class no {
    public static void main(String[] args) {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testing", "root", "20821362");
            if (con != null)
                System.out.println("Database is connected.");
        } catch (Exception e) {
            System.out.println("Not connected: " + e.getMessage());
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
