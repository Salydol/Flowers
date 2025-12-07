package database.dao;

import Model.Decoration;
import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DecorationDAO {
    
    private String lastError = null;
    public List<Decoration> getAllDecorations() {
        List<Decoration> decorations = new ArrayList<>();
        String query = "SELECT * FROM decorations ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Decoration deco = new Decoration(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("image_path")
                );
                decorations.add(deco);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return decorations;
    }

    public boolean addDecoration(Decoration decoration) {
        String query = "INSERT INTO decorations (name, price, image_path) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, decoration.getName());
            pstmt.setInt(2, decoration.getPrice());
            pstmt.setString(3, decoration.getImagePath());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // SQLite: getGeneratedKeys не поддерживается, используем last_insert_rowid()
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        decoration.setId(rs.getInt(1));
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

    public boolean updateDecoration(Decoration decoration) {
        String query = "UPDATE decorations SET name = ?, price = ?, image_path = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, decoration.getName());
            pstmt.setInt(2, decoration.getPrice());
            pstmt.setString(3, decoration.getImagePath());
            pstmt.setInt(4, decoration.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteDecoration(int id) {
        String query = "DELETE FROM decorations WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Decoration getDecorationById(int id) {
        String query = "SELECT * FROM decorations WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Decoration(
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