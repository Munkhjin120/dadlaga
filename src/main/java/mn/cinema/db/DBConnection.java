package mn.cinema.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * MySQL өгөгдлийн сантай холбогдоход ашиглана.
 * Тохиргоог classpath дахь /db.properties файлаас уншина.
 */
public class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/cinema_booking?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true");
        USER = props.getProperty("db.user", "root");
        PASSWORD = props.getProperty("db.password", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
