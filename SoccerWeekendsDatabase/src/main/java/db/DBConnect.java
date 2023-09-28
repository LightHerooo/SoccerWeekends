package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnect{
    private static String configPath = String.format("%s/%s", System.getProperty("user.dir"), "dbConnectConfig.conf");
    private static String dbURLKey = "DB_URL";
    private static String dbNameKey = "DB_NAME";
    private static String userKey = "DB_USER_LOGIN";
    private static String passwordKey = "DB_USER_PASSWORD";

    public static Connection getConnection() throws SQLException {
        String dbURLPattern = "jdbc:sqlserver://%s;databaseName=%s;trustServerCertificate=true;user=%s;password=%s";

        tryToCreateConfig();
        Properties properties = readConfig();
        dbURLPattern = String.format(dbURLPattern,
                properties.getProperty(dbURLKey),
                properties.getProperty(dbNameKey),
                properties.getProperty(userKey),
                properties.getProperty(passwordKey));

        return DriverManager.getConnection(dbURLPattern);
    }

    private static void tryToCreateConfig() {
        File file = new File(configPath);
        if (!file.exists()) {
            Properties properties = new Properties();
            properties.setProperty(dbURLKey, "localhost:1433");
            properties.setProperty(dbNameKey, "SoccerWeekends");
            properties.setProperty(userKey, "Test");
            properties.setProperty(passwordKey, "00000");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                properties.store(fos, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Properties readConfig() {
        File file = new File(configPath);
        Properties properties = new Properties();
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return properties;
    }
}
