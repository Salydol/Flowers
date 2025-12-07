package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // SQLite использует файл базы данных
    // Используем абсолютный путь для надежности
    private static final String DB_PATH = System.getProperty("user.dir") + File.separator + "flower_shop.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                // Включаем поддержку внешних ключей в SQLite
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
                System.out.println("Подключение к базе данных SQLite успешно! Файл: " + DB_PATH);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver не найден!");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных!");
            System.err.println("URL: " + URL);
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getDbPath() {
        return DB_PATH;
    }
}
