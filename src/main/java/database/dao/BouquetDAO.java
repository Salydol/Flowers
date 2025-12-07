package database.dao;

import Model.*;
import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BouquetDAO {

    // Сохранить букет со всеми деталями
    public boolean saveBouquet(String bouquetName, List<BouquetItem> flowers,
                               Packaging packaging, List<Decoration> decorations, int totalPrice) {

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Начинаем транзакцию

            // 1. Сохраняем основную запись букета
            String bouquetQuery = "INSERT INTO bouquets (name, total_price, packaging_id) VALUES (?, ?, ?)";
            int bouquetId;

            try (PreparedStatement pstmt = conn.prepareStatement(bouquetQuery)) {
                pstmt.setString(1, bouquetName);
                pstmt.setInt(2, totalPrice);
                if (packaging != null) {
                    pstmt.setInt(3, packaging.getId());
                } else {
                    pstmt.setNull(3, Types.INTEGER);
                }

                pstmt.executeUpdate();

                // Для SQLite используем last_insert_rowid()
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        bouquetId = rs.getInt(1);
                    } else {
                        throw new SQLException("Не удалось получить ID букета");
                    }
                }
            }

            // 2. Сохраняем цветы в букете
            String flowerQuery = "INSERT INTO bouquet_flowers (bouquet_id, flower_id, quantity, price_at_moment) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(flowerQuery)) {
                for (BouquetItem item : flowers) {
                    pstmt.setInt(1, bouquetId);
                    pstmt.setInt(2, item.getFlower().getId());
                    pstmt.setInt(3, item.getQuantity());
                    pstmt.setInt(4, item.getFlower().getPrice());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // 3. Сохраняем декорации
            if (decorations != null && !decorations.isEmpty()) {
                String decoQuery = "INSERT INTO bouquet_decorations (bouquet_id, decoration_id, price_at_moment) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(decoQuery)) {
                    for (Decoration deco : decorations) {
                        pstmt.setInt(1, bouquetId);
                        pstmt.setInt(2, deco.getId());
                        pstmt.setInt(3, deco.getPrice());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }

            conn.commit(); // Подтверждаем транзакцию
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Откатываем при ошибке
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Получить все букеты
    public List<BouquetInfo> getAllBouquets() {
        List<BouquetInfo> bouquets = new ArrayList<>();
        String query = "SELECT b.*, p.name as packaging_name FROM bouquets b " +
                "LEFT JOIN packaging p ON b.packaging_id = p.id " +
                "ORDER BY b.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                BouquetInfo info = new BouquetInfo(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("total_price"),
                        rs.getString("packaging_name"),
                        rs.getTimestamp("created_at")
                );
                bouquets.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bouquets;
    }

    // Получить детали букета
    public BouquetDetails getBouquetDetails(int bouquetId) {
        BouquetDetails details = new BouquetDetails();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Получаем цветы
            String flowersQuery = "SELECT f.name, bf.quantity, bf.price_at_moment " +
                    "FROM bouquet_flowers bf " +
                    "JOIN flowers f ON bf.flower_id = f.id " +
                    "WHERE bf.bouquet_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(flowersQuery)) {
                pstmt.setInt(1, bouquetId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        details.addFlower(
                                rs.getString("name"),
                                rs.getInt("quantity"),
                                rs.getInt("price_at_moment")
                        );
                    }
                }
            }

            // Получаем декорации
            String decosQuery = "SELECT d.name, bd.price_at_moment " +
                    "FROM bouquet_decorations bd " +
                    "JOIN decorations d ON bd.decoration_id = d.id " +
                    "WHERE bd.bouquet_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(decosQuery)) {
                pstmt.setInt(1, bouquetId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        details.addDecoration(
                                rs.getString("name"),
                                rs.getInt("price_at_moment")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return details;
    }

    // Вспомогательные классы для хранения информации о букетах
    public static class BouquetInfo {
        private int id;
        private String name;
        private int totalPrice;
        private String packagingName;
        private Timestamp createdAt;

        public BouquetInfo(int id, String name, int totalPrice, String packagingName, Timestamp createdAt) {
            this.id = id;
            this.name = name;
            this.totalPrice = totalPrice;
            this.packagingName = packagingName;
            this.createdAt = createdAt;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getTotalPrice() { return totalPrice; }
        public String getPackagingName() { return packagingName; }
        public Timestamp getCreatedAt() { return createdAt; }
    }

    public static class BouquetDetails {
        private List<FlowerDetail> flowers = new ArrayList<>();
        private List<DecorationDetail> decorations = new ArrayList<>();

        public void addFlower(String name, int quantity, int price) {
            flowers.add(new FlowerDetail(name, quantity, price));
        }

        public void addDecoration(String name, int price) {
            decorations.add(new DecorationDetail(name, price));
        }

        public List<FlowerDetail> getFlowers() { return flowers; }
        public List<DecorationDetail> getDecorations() { return decorations; }
    }

    public static class FlowerDetail {
        String name;
        int quantity;
        int price;

        public FlowerDetail(String name, int quantity, int price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public int getPrice() { return price; }
    }

    public static class DecorationDetail {
        String name;
        int price;

        public DecorationDetail(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public String getName() { return name; }
        public int getPrice() { return price; }
    }
}