package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    private static boolean initialized = false;


    public static void initializeDatabase() {
        if (initialized) {
            System.out.println("База данных уже инициализирована.");
            return;
        }

        String dbPath = DatabaseConnection.getDbPath();
        String url = "jdbc:sqlite:" + dbPath;
        
        System.out.println("Начинаем инициализацию базы данных...");
        System.out.println("Путь к БД: " + dbPath);

        // Создаем отдельное соединение для инициализации
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            System.out.println("Соединение с БД установлено.");

            // Включаем поддержку внешних ключей (должно быть первым)
            stmt.execute("PRAGMA foreign_keys = ON");
            System.out.println("Внешние ключи включены.");

            // Создание таблиц
            System.out.println("Создание таблицы flowers...");
            stmt.execute("CREATE TABLE IF NOT EXISTS flowers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "price INTEGER NOT NULL, " +
                    "image_path VARCHAR(255), " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("Таблица flowers создана.");

            System.out.println("Создание таблицы packaging...");
            stmt.execute("CREATE TABLE IF NOT EXISTS packaging (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "price INTEGER NOT NULL, " +
                    "image_path VARCHAR(255), " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("Таблица packaging создана.");

            System.out.println("Создание таблицы decorations...");
            stmt.execute("CREATE TABLE IF NOT EXISTS decorations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "price INTEGER NOT NULL, " +
                    "image_path VARCHAR(255), " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("Таблица decorations создана.");

            System.out.println("Создание таблицы bouquets...");
            stmt.execute("CREATE TABLE IF NOT EXISTS bouquets (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(200), " +
                    "total_price INTEGER NOT NULL, " +
                    "packaging_id INTEGER REFERENCES packaging(id) ON DELETE SET NULL, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("Таблица bouquets создана.");

            System.out.println("Создание таблицы bouquet_flowers...");
            stmt.execute("CREATE TABLE IF NOT EXISTS bouquet_flowers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "bouquet_id INTEGER NOT NULL REFERENCES bouquets(id) ON DELETE CASCADE, " +
                    "flower_id INTEGER NOT NULL REFERENCES flowers(id) ON DELETE CASCADE, " +
                    "quantity INTEGER NOT NULL, " +
                    "price_at_moment INTEGER NOT NULL)");
            System.out.println("Таблица bouquet_flowers создана.");

            System.out.println("Создание таблицы bouquet_decorations...");
            stmt.execute("CREATE TABLE IF NOT EXISTS bouquet_decorations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "bouquet_id INTEGER NOT NULL REFERENCES bouquets(id) ON DELETE CASCADE, " +
                    "decoration_id INTEGER NOT NULL REFERENCES decorations(id) ON DELETE CASCADE, " +
                    "price_at_moment INTEGER NOT NULL)");
            System.out.println("Таблица bouquet_decorations создана.");

            // Проверяем, что таблицы созданы
            System.out.println("Проверка создания таблиц...");
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "flowers", null)) {
                if (rs.next()) {
                    System.out.println("✓ Таблица flowers существует");
                } else {
                    System.err.println("✗ Таблица flowers НЕ найдена!");
                }
            }

            try (ResultSet rs = conn.getMetaData().getTables(null, null, "packaging", null)) {
                if (rs.next()) {
                    System.out.println("✓ Таблица packaging существует");
                } else {
                    System.err.println("✗ Таблица packaging НЕ найдена!");
                }
            }

            // Вставка начальных данных (если их еще нет)
            System.out.println("Вставка начальных данных...");
            
            // Проверяем количество записей перед вставкой
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM flowers")) {
                rs.next();
                int flowerCount = rs.getInt(1);
                if (flowerCount == 0) {
                    stmt.execute("INSERT INTO flowers (name, price, image_path) VALUES " +
                            "('Роза', 500, 'images/rose.png'), " +
                            "('Тюльпан', 300, 'images/tulip.png'), " +
                            "('Пион', 700, 'images/peony.png'), " +
                            "('Хризантема', 400, 'images/chrys.png')");
                    System.out.println("Начальные данные для flowers вставлены.");
                } else {
                    System.out.println("Данные flowers уже существуют (" + flowerCount + " записей). Пропуск вставки.");
                }
            }

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM packaging")) {
                rs.next();
                int packagingCount = rs.getInt(1);
                if (packagingCount == 0) {
                    stmt.execute("INSERT INTO packaging (name, price, image_path) VALUES " +
                            "('Крафт бумага', 300, 'images/pack_craft.png'), " +
                            "('Белая матовая', 500, 'images/pack_white.png'), " +
                            "('Коробка', 1500, 'images/pack_box.png'), " +
                            "('Шелковая лента', 200, 'images/pack_ribbon.png')");
                    System.out.println("Начальные данные для packaging вставлены.");
                } else {
                    System.out.println("Данные packaging уже существуют (" + packagingCount + " записей). Пропуск вставки.");
                }
            }

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM decorations")) {
                rs.next();
                int decorationCount = rs.getInt(1);
                if (decorationCount == 0) {
                    stmt.execute("INSERT INTO decorations (name, price, image_path) VALUES " +
                            "('Блёстки', 200, 'images/deco_glitter.png'), " +
                            "('Сердечки', 150, 'images/deco_heart.png'), " +
                            "('Мини-открытка', 300, 'images/deco_card.png')");
                    System.out.println("Начальные данные для decorations вставлены.");
                } else {
                    System.out.println("Данные decorations уже существуют (" + decorationCount + " записей). Пропуск вставки.");
                }
            }

            // Проверяем количество записей
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM flowers")) {
                if (rs.next()) {
                    System.out.println("Количество цветов в БД: " + rs.getInt(1));
                }
            }

            initialized = true;
            System.out.println("========================================");
            System.out.println("База данных SQLite успешно инициализирована!");
            System.out.println("Файл БД: " + dbPath);
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("ОШИБКА при инициализации базы данных!");
            System.err.println("Сообщение: " + e.getMessage());
            System.err.println("========================================");
            e.printStackTrace();
            throw new RuntimeException("Не удалось инициализировать базу данных", e);
        }
    }
}
