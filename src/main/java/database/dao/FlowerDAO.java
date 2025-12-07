package database.dao;

import Model.Flower;
import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlowerDAO {
    
    private String lastError = null;
    // Получить все цветы
    public List<Flower> getAllFlowers() {
        List<Flower> flowers = new ArrayList<>();
        String query = "SELECT * FROM flowers ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Flower flower = new Flower(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("image_path")
                );
                flowers.add(flower);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flowers;
    }

    // Добавить новый цветок
    public boolean addFlower(Flower flower) {
        String query = "INSERT INTO flowers (name, price, image_path) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, flower.getName());
            pstmt.setInt(2, flower.getPrice());
            pstmt.setString(3, flower.getImagePath());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // SQLite JDBC драйвер не поддерживает getGeneratedKeys(), используем last_insert_rowid()
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        flower.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
        }

        return false;
    }

    public String getLastError() {
        return lastError;
    }

    // Обновить цветок
    public boolean updateFlower(Flower flower) {
        String query = "UPDATE flowers SET name = ?, price = ?, image_path = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, flower.getName());
            pstmt.setInt(2, flower.getPrice());
            pstmt.setString(3, flower.getImagePath());
            pstmt.setInt(4, flower.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Удалить цветок
    public boolean deleteFlower(int id) {
        String query = "DELETE FROM flowers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Получить цветок по ID
    public Flower getFlowerById(int id) {
        String query = "SELECT * FROM flowers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Flower(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("price"),
                            rs.getString("image_path")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}