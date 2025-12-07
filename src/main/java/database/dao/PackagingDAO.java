package database.dao;

import Model.Packaging;
import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackagingDAO {
    
    private String lastError = null;
    // Получить все упаковки из БД
    public List<Packaging> getAllPackaging() {
        List<Packaging> packagingList = new ArrayList<>();
        String query = "SELECT * FROM packaging ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Packaging pkg = new Packaging(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("image_path")
                );
                packagingList.add(pkg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return packagingList;
    }

    // Добавить новую упаковку
    public boolean addPackaging(Packaging packaging) {
        String query = "INSERT INTO packaging (name, price, image_path) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, packaging.getName());
            pstmt.setInt(2, packaging.getPrice());
            pstmt.setString(3, packaging.getImagePath());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // SQLite: getGeneratedKeys не поддерживается, используем last_insert_rowid()
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        packaging.setId(rs.getInt(1));
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

    // Обновить упаковку
    public boolean updatePackaging(Packaging packaging) {
        String query = "UPDATE packaging SET name = ?, price = ?, image_path = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, packaging.getName());
            pstmt.setInt(2, packaging.getPrice());
            pstmt.setString(3, packaging.getImagePath());
            pstmt.setInt(4, packaging.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Удалить упаковку
    public boolean deletePackaging(int id) {
        String query = "DELETE FROM packaging WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Получить упаковку по ID
    public Packaging getPackagingById(int id) {
        String query = "SELECT * FROM packaging WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Packaging(
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