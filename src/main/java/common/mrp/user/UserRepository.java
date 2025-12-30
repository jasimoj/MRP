package common.mrp.user;

import common.ConnectionPool;
import common.database.Repository;
import common.exception.EntityNotFoundException;
import common.mrp.leaderboard.LeaderboardEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.database.Repository.SQL_ALREADY_EXISTS_CODE;


public class UserRepository {
    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID
            = "SELECT id, username, email, password, favorite_genre FROM users WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT id, username, email, password, favorite_genre  FROM users ORDER BY id";

    private static final String SELECT_BY_USERNAME =
            "SELECT id, username, email, password, favorite_genre  FROM users WHERE username = ?";

    private static final String INSERT_USER =
            "INSERT INTO users (username, email, password, favorite_genre) VALUES (?, ?, ?, ?) RETURNING id, username, email, password, favorite_genre";

    private static final String UPDATE_USER =
            "UPDATE users SET username = ?, email = ?, password = ?, favorite_genre = ? WHERE id = ? RETURNING id, username, email, password, favorite_genre";

    private static final String DELETE_USER =
            "DELETE FROM users WHERE id = ?";

    private static final String SELECT_PROFILE =
            "SELECT u.id, u.username, u.email, u.favorite_genre, COALESCE(COUNT(r.id), 0) AS total_ratings, COALESCE(AVG(r.stars), 0) AS avg_stars FROM users u LEFT JOIN ratings r ON r.user_id = u.id WHERE u.id = ? GROUP BY u.id, u.username, u.email, u.favorite_genre";

    private static final String GET_LEADERBOARD =
            "SELECT u.id, u.username, COUNT(r.id) as ratings_count FROM users u LEFT JOIN ratings r ON r.user_id = u.id GROUP BY u.id, u.username ORDER BY ratings_count DESC, u.username ASC LIMIT 10 ";

    public UserRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setFavoriteGenre(rs.getString("favorite_genre"));
        return u;
    }

    private static UserProfile userProfileMap(ResultSet rs) throws SQLException {
        UserProfile up = new UserProfile();
        up.setId(rs.getInt("id"));
        up.setUsername(rs.getString("username"));
        up.setEmail(rs.getString("email"));
        up.setFavoriteGenre(rs.getString("favorite_genre"));
        up.setTotalRatings(rs.getInt("total_ratings"));
        up.setAvgStars(rs.getDouble("avg_stars"));
        return up;
    }

    private static LeaderboardEntry leaderboardMap(ResultSet rs) throws SQLException {
        LeaderboardEntry le = new LeaderboardEntry();
        le.setUserid(rs.getInt("id"));
        le.setUsername(rs.getString("username"));
        le.setRatingsCount(rs.getInt("ratings_count"));
        return le;
    }

    public Optional<User> find(Integer id) {
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

    public List<User> findAll() {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<User> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
    }

    public User save(User user) {
        if (user.getId() == 0) {
            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement(INSERT_USER)) {

                ps.setString(1, user.getUsername());

                if (user.getEmail() == null) {
                    ps.setNull(2, java.sql.Types.VARCHAR);
                } else {
                    ps.setString(2, user.getEmail());
                }

                ps.setString(3, user.getPassword());

                if (user.getFavoriteGenre() == null) {
                    ps.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    ps.setString(4, user.getFavoriteGenre());
                }

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return map(rs);
                    }
                }

                throw new RuntimeException("Unexpected: INSERT returned no row");

            } catch (SQLException e) {
                if (SQL_ALREADY_EXISTS_CODE.equals(e.getSQLState())) {
                    throw new RuntimeException("Username already exists");
                }
                throw new RuntimeException("insert user failed: " + e.getMessage(), e);
            }
        }

        // UPDATE
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER)) {

            ps.setString(1, user.getUsername());
            if (user.getEmail() == null) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, user.getEmail());
            }

            ps.setString(3, user.getPassword());

            if (user.getFavoriteGenre() == null) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, user.getFavoriteGenre());
            }
            ps.setInt(5, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
            throw new RuntimeException("update returned no row (user not found?)");

        } catch (SQLException e) {
            throw new RuntimeException("update user failed", e);
        }
    }

    public void delete(Integer id) {
        if (find(id).isEmpty()) {
            throw  new EntityNotFoundException("No entity found with id: " + id);
        }

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_USER)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("delete failed for id=" + id, e);
        }
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUsername failed: " + username, e);
        }
    }

    public List<LeaderboardEntry>  getLeaderboardByRatings(){
        try(Connection conn = connectionPool.getConnection();
        PreparedStatement ps = conn.prepareStatement(GET_LEADERBOARD)){
            List<LeaderboardEntry> leaderboard = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    leaderboard.add(leaderboardMap(rs));
                }
                return leaderboard;
            }
        }catch (SQLException e){
            throw new RuntimeException("getLeaderboardByRatings failed" + e.getMessage(), e);
        }
    }


    public Optional<UserProfile> getProfile(Integer id) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PROFILE)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(userProfileMap(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("finding userprofile failed"+ e.getMessage(), e);
        }
    }
}
