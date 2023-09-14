package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect{
    public static Connection getConnection() throws SQLException {
        //String dbURLPattern = "jdbc:sqlserver://%s;databaseName=%s;trustServerCertificate=true;integratedSecurity=true;";
        String dbURLPattern = "jdbc:sqlserver://%s;databaseName=%s;trustServerCertificate=true;user=%s;password=%s";
        String dbURL = "localhost\\SQLEXPRESS";
        String dbName = "SoccerWeekends";
        String user = "Test";
        String password = "00000";

        dbURLPattern = String.format(dbURLPattern, dbURL, dbName, user, password);
        return DriverManager.getConnection(dbURLPattern);
    }
}
