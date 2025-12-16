package common.mrp.favorite;

import common.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class FavoriteRepository {
    private final ConnectionPool connectionPool;

    private static final String INSERT_FAVORITE =
            "INSERT INTO favorites (user_id, media_id) VALUES (?, ?)";

    private static final String DELETE_FAVORITE =
            "DELETE FROM favorites WHERE user_id = ? AND media_id = ?";

    public FavoriteRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void save(int userId, int mediaId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_FAVORITE)) {
            ps.setInt(1, userId);
            ps.setInt(2, mediaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("mark as favorite failed");
        }
    }

    public void delete(int userId, int mediaId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_FAVORITE)) {
            ps.setInt(1, userId);
            ps.setInt(2, mediaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("unmark as favorite failed");
        }
    }
}
