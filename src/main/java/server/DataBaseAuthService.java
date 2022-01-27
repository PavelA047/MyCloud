package server;

import java.sql.*;

public class DataBaseAuthService {
    public static final String USER_NAME = "root";
    public static final String PASSWORD = "root";
    public static final String URL = "jdbc:mysql://localhost:3306/clients";
    public static Connection connection;

    static {
        try {
            connect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
    }

    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registration(String fn, String ln, String log, String pass, String loc, String gen) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO users (first_name, last_name," +
                    "login, password, location, gender) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, fn);
            pstmt.setString(2, ln);
            pstmt.setString(3, log);
            pstmt.setString(4, pass);
            pstmt.setString(5, loc);
            pstmt.setString(6, gen);
            pstmt.execute();
        } catch (SQLException troubles) {
            return false;
        }
        return true;
    }

    public String getPasByLogin(String log) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT password FROM clients.users WHERE login = ?");
            pstmt.setString(1, log);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getString(1);
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
        return null;
    }
}
