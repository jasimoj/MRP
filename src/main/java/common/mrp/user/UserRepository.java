package common.mrp.user;

import common.ConnectionPool;
import common.database.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepository implements Repository<User, Integer> {
    private final List<User> users;
    private int firstIdForNow = 1;
    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID
            = "SELECT * FROM todos WHERE id = ?";

    public UserRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        users = new ArrayList<>();
    }

    @Override
    public Optional<User> find(Integer id) {
        try(Connection conn = connectionPool.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)){

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.getResultSet()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFavoriteGenre(rs.getString("favoriteGenre"));

                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User save(User user) {
        if (user.getId() == 0) { // neue ID, wenn noch keine gesetzt
            user.setId(firstIdForNow++);
            users.add(user);
        } else {
            // vorhandenen ersetzen
            find(user.getId()).ifPresentOrElse(existing -> {
                existing.setUsername(user.getUsername());
                existing.setEmail(user.getEmail());
                existing.setFavoriteGenre(user.getFavoriteGenre());
                existing.setPassword(user.getPassword());
            }, () -> users.add(user));
        }
        return user;
    }

    @Override
    public User delete(Integer id) {
       return null; // Wird nicht gebraucht, refactor?
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return users.stream().filter(u -> username.equals(u.getUsername())).findFirst();
    }
}
