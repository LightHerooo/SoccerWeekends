package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect{
    public static Connection getConnection() throws SQLException {
        String dbURLPattern = "jdbc:sqlserver://%s;databaseName=%s;trustServerCertificate=true;integratedSecurity=true;";
        String dbURL = "localhost\\SQLEXPRESS";
        String dbName = "SoccerWeekends";

        dbURLPattern = String.format(dbURLPattern, dbURL, dbName);
        return DriverManager.getConnection(dbURLPattern);
    }
}
