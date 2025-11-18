package common.mrp.rating;

import common.ConnectionPool;
import common.database.Repository;
import common.exception.EntityNotFoundException;
import common.mrp.media.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RatingRepository implements Repository<Rating, Integer> {
    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID
            = "SELECT * FROM rating WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM rating ORDER BY id";

    private static final String SELECT_ALL_RATINGS_FROM_MEDIA =
            "SELECT * FROM rating WHERE media_id = ?";

    private static final String SELECT_ALL_RATINGS_FROM_USER =
            "SELECT * FROM rating WHERE user_id = ?";

    private static final String INSERT_RATING =
            "INSERT INTO rating (media_id, user_id, stars, comment, confirmed) VALUES (?, ?, ?, ?, ?) RETURNING *";

    private static final String UPDATE_RATING =
            "UPDATE rating SET stars = ?, comment = ?, confirmed = ? WHERE id = ?";

    private static final String DELETE_RATING =
            "DELETE FROM rating WHERE id = ?";


    public RatingRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private static Rating map(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getInt("id"));
        rating.setMediaId(rs.getInt("media_id"));
        rating.setUserId(rs.getInt("user_id"));
        rating.setStars(rs.getInt("stars"));
        rating.setComment(rs.getString("comment"));
        rating.setConfirmed(rs.getBoolean("confirmed"));

        return rating;
    }

    @Override
    public Optional<Rating> find(Integer id) {
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
    public List<Rating> findAll() {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Rating> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }    }

    @Override
    public Rating save(Rating rating) {
        // Insert NEW Rating
        if (rating.getId() == 0) {
            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement(INSERT_RATING)) {

                ps.setInt(1, rating.getMediaId());
                ps.setInt(2, rating.getUserId());
                ps.setInt(3, rating.getStars());
                ps.setString(4, rating.getComment());
                ps.setBoolean(5, rating.isConfirmed());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return map(rs);
                }
                throw new RuntimeException("Unexpected: INSERT returned no row");
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    throw new RuntimeException("Rating already exists");
                }
                throw new RuntimeException("insert rating failed: " + e.getMessage(), e);
            }
        }

        // UPDATE (bestehendes Rating)
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_RATING)) {

            ps.setInt(1, rating.getUserId());
            ps.setInt(2, rating.getStars());
            ps.setString(3, rating.getComment());
            ps.setBoolean(4, rating.isConfirmed());

            try (ResultSet rs = ps.executeQuery()) {        // wegen RETURNING *
                if (rs.next()) return map(rs);
            }
            throw new RuntimeException("update returned no row (rating not found?)");
        } catch (SQLException e) {
            throw new RuntimeException("update rating failed", e);
        }
    }

    @Override
    public Rating delete(Integer id) {
        Optional<Rating> existing = find(id);
        if (existing.isEmpty()) return null;

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_RATING)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return existing.get();

        } catch (SQLException e) {
            throw new RuntimeException("delete failed for id=" + id, e);
        }
    }
}
