package common.mrp.media;

import common.ConnectionPool;
import common.database.Repository;
import common.exception.EntityNotFoundException;
import common.mrp.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MediaRepository implements Repository<Media, Integer> {
    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID
            = "SELECT * FROM media WHERE id = ?";
    private static final String SELECT_ALL
                       = "SELECT * FROM media ORDER BY id";
    private static final String INSERT_MEDIA =
            "INSERT INTO media (title, media_type, release_year, age_restriction, description, created_by_user_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING *";

    private static final String UPDATE_MEDIA =
            "UPDATE media SET title = ?, media_type = ?, release_year = ?, age_restriction = ?, " +
                    "description = ?, created_by_user_id = ? WHERE id = ? RETURNING *";

    private static final String DELETE_MEDIA =
            "DELETE FROM media WHERE id = ?";

    public MediaRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private static Media map(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId(rs.getInt("id"));
        media.setTitle(rs.getString("title"));
        media.setMediaType(rs.getString("media_type"));
        media.setReleaseYear(rs.getInt("release_year"));
        media.setAgeRestriction(rs.getInt("age_restriction"));
        media.setDescription(rs.getString("description"));
        media.setCreatedByUserId(rs.getInt("created_by_user_id"));

        return media;
    }

    @Override
    public Optional<Media> find(Integer id) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("find failed", e);
        }
    }

    @Override
    public List<Media> findAll() {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Media> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
    }

    @Override
    public Media save(Media media) {
        // Insert NEW Media
        if (media.getId() == 0) {
            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement(INSERT_MEDIA)) {

                ps.setString(1, media.getTitle());
                ps.setString(2, media.getMediaType());
                ps.setInt(3, media.getReleaseYear());
                ps.setInt(4, media.getAgeRestriction());
                ps.setString(5, media.getDescription());
                ps.setInt(6, media.getCreatedByUserId());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return map(rs);
                }
                throw new RuntimeException("Unexpected: INSERT returned no row");
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    throw new RuntimeException("Media already exists");
                }
                throw new RuntimeException("insert media failed: " + e.getMessage(), e);
            }
        }

        // UPDATE (bestehendes Media)
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_MEDIA)) {

            ps.setString(1, media.getTitle());
            ps.setString(2, media.getMediaType());
            ps.setInt(3, media.getReleaseYear());
            ps.setInt(4, media.getAgeRestriction());
            ps.setString(5, media.getDescription());
            ps.setInt(6, media.getCreatedByUserId());
            ps.setInt(7, media.getId());

            try (ResultSet rs = ps.executeQuery()) {        // wegen RETURNING *
                if (rs.next()) return map(rs);
            }
            throw new RuntimeException("update returned no row (media not found?)");
        } catch (SQLException e) {
            throw new RuntimeException("update media failed", e);
        }
    }

    @Override
    public Media delete(Integer id) {
        Optional<Media> existing = find(id);
        if (existing.isEmpty()) return null;

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_MEDIA)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return existing.get();

        } catch (SQLException e) {
            throw new RuntimeException("delete failed for id=" + id, e);
        }
    }
}
