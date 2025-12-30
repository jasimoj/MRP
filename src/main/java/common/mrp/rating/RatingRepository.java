package common.mrp.rating;

import common.ConnectionPool;
import common.database.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.database.Repository.SQL_ALREADY_EXISTS_CODE;

public class RatingRepository {
    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID
            = "SELECT r.id, r.user_id, r.media_id, r.comment, r.stars, r.confirmed, r.created_at, (SELECT COUNT(*) FROM rating_likes rl WHERE rl.rating_id = r.id) AS likes_count FROM ratings r WHERE r.id = ?";

    private static final String SELECT_BY_MEDIA_ID =
            "SELECT r.id, r.user_id, r.media_id, r.comment, r.stars, r.confirmed, r.created_at, (SELECT COUNT(*) FROM rating_likes rl WHERE rl.rating_id = r.id) AS likes_count FROM ratings r WHERE r.media_id = ? ORDER BY r.created_at DESC";

    private static final String FIND_ALL_BY_USER_ID
            = "SELECT r.id, r.user_id, r.media_id, r.comment, r.stars, r.confirmed, r.created_at, (SELECT COUNT(*) FROM rating_likes rl WHERE rl.rating_id = r.id) AS likes_count FROM ratings r WHERE r.user_id = ?";

    private static final String INSERT_RATING =
            "INSERT INTO ratings (user_id, media_id, comment, stars, confirmed, created_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id, user_id, media_id, comment, stars, confirmed, created_at";


    private static final String UPDATE_RATING =
            "UPDATE ratings SET comment = ?, stars = ?, confirmed = ? WHERE id = ? RETURNING id, user_id, media_id, comment, stars, confirmed, created_at";

    private static final String DELETE_RATING =
            "DELETE FROM ratings WHERE id = ?";

    private static final String INSERT_LIKE =
            "INSERT INTO rating_likes (rating_id, user_id) VALUES (?, ?)";


    public RatingRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private static Rating map(ResultSet rs, boolean withLike) throws SQLException {
        Rating r = new Rating();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setMediaId(rs.getInt("media_id"));
        r.setComment(rs.getString("comment"));
        r.setStars(rs.getInt("stars"));
        r.setConfirmed(rs.getBoolean("confirmed"));
        r.setCreatedAt(rs.getLong("created_at"));
        if(withLike) {
            r.setLikesCount(rs.getInt("likes_count"));
        }
        return r;
    }

    public Optional<Rating> find(Integer id) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs, true));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("find rating failed" + e.getMessage(), e);
        }
    }

    public List<Rating> findByMediaId(int id) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_MEDIA_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                List<Rating> ratings = new ArrayList<>();
                while (rs.next()) {
                    ratings.add(map(rs, true));
                }
                return ratings;
            }
        } catch (SQLException e) {
            throw new RuntimeException("find rating by mediaId failed" + e.getMessage(), e);
        }
    }

    public List<Rating> findAll(Integer userId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_BY_USER_ID)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Rating> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs, true));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
    }

    public Rating save(Rating rating) {
        // Insert NEW Rating
        if (rating.getId() == 0) {
            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement(INSERT_RATING)) {
                ps.setInt(1, rating.getUserId());
                ps.setInt(2, rating.getMediaId());

                if (rating.getComment() == null) ps.setNull(3, Types.VARCHAR);
                else ps.setString(3, rating.getComment());

                ps.setInt(4, rating.getStars());
                ps.setBoolean(5, rating.isConfirmed());
                ps.setLong(6, rating.getCreatedAt());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return map(rs, false);
                }
                throw new RuntimeException("Unexpected: INSERT returned no row");
            } catch (SQLException e) {
                if (SQL_ALREADY_EXISTS_CODE.equals(e.getSQLState())) {
                    throw new RuntimeException("Rating already exists");
                }
                throw new RuntimeException("insert rating failed: " + e.getMessage(), e);
            }
        }

        // UPDATE
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_RATING)) {

            if (rating.getComment() == null) ps.setNull(1, Types.VARCHAR);
            else ps.setString(1, rating.getComment());

            ps.setInt(2, rating.getStars());
            ps.setBoolean(3, rating.isConfirmed());
            ps.setInt(4, rating.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, false);
            }
            throw new RuntimeException("update returned no row (rating not found?)");
        } catch (SQLException e) {
            throw new RuntimeException("update rating failed" + e.getMessage(), e);
        }
    }

    public void delete(Integer id) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_RATING)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("delete rating failed" + e.getMessage(), e);
        }
    }

    public void like(int ratingId, int userId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_LIKE)) {

            ps.setInt(1, ratingId);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            if (SQL_ALREADY_EXISTS_CODE.equals(e.getSQLState())) return;
            throw new RuntimeException("like failed" + e.getMessage(), e);
        }
    }

}
