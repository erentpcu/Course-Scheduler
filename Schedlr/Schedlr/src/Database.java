import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection connect() {
        String url = "jdbc:sqlite:university.db"; // Path to your SQLite database
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Connection to SQLite failed: " + e.getMessage());
        }

        return conn;
    }
}