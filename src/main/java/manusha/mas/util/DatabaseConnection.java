package manusha.mas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/petshop"; // Replace with your database name
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "manusha123"; // Replace with your MySQL password

    // Method to establish and return a connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}